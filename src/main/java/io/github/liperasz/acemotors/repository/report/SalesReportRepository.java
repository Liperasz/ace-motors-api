package io.github.liperasz.acemotors.repository.report;

import io.github.liperasz.acemotors.dto.report.SalesByCategory;
import io.github.liperasz.acemotors.dto.report.SalesByProduct;
import io.github.liperasz.acemotors.entity.*;
import io.github.liperasz.acemotors.enums.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// Consultas de relatório (RF07) com Criteria API: agrupam e somam direto no banco,
// montando os DTOs com builder.construct (como o porCategoria/porDia do projeto do professor)
@Repository
public class SalesReportRepository {

    // Só conta como venda pedido que já foi pago
    private static final List<OrderStatus> SOLD = List.of(OrderStatus.PAID, OrderStatus.SHIPPED,
            OrderStatus.DELIVERED);

    @PersistenceContext
    private EntityManager manager;

    // Pedidos vendidos no período (o agrupamento por dia é feito no service, no fuso do servidor)
    public List<Orders> soldOrders(Instant start, Instant end) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Orders> criteria = builder.createQuery(Orders.class);
        Root<Orders> root = criteria.from(Orders.class);

        criteria.where(soldBetween(builder, root, start, end));

        return manager.createQuery(criteria).getResultList();
    }

    public List<SalesByProduct> byProduct(Instant start, Instant end) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<SalesByProduct> criteria = builder.createQuery(SalesByProduct.class);
        Root<OrderItem> item = criteria.from(OrderItem.class);
        Join<OrderItem, Orders> order = item.join("order");
        Join<OrderItem, Product> product = item.join("product");

        criteria.select(builder.construct(SalesByProduct.class,
                product.get("id"), product.get("name"),
                builder.sumAsLong(item.get("quantity")),
                builder.sum(item.<BigDecimal>get("subtotal"))));
        criteria.where(soldBetween(builder, order, start, end));
        criteria.groupBy(product.get("id"), product.get("name"));
        criteria.orderBy(builder.desc(builder.sum(item.<BigDecimal>get("subtotal"))));

        return manager.createQuery(criteria).getResultList();
    }

    public List<SalesByCategory> byCategory(Instant start, Instant end) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<SalesByCategory> criteria = builder.createQuery(SalesByCategory.class);
        Root<OrderItem> item = criteria.from(OrderItem.class);
        Join<OrderItem, Orders> order = item.join("order");
        Join<Product, Category> category = item.join("product").join("categories");

        criteria.select(builder.construct(SalesByCategory.class,
                category.get("id"), category.get("name"),
                builder.sumAsLong(item.get("quantity")),
                builder.sum(item.<BigDecimal>get("subtotal"))));
        criteria.where(soldBetween(builder, order, start, end));
        criteria.groupBy(category.get("id"), category.get("name"));
        criteria.orderBy(builder.desc(builder.sum(item.<BigDecimal>get("subtotal"))));

        return manager.createQuery(criteria).getResultList();
    }

    // status vendido E start <= createdAt < end
    private Predicate soldBetween(CriteriaBuilder builder, From<?, Orders> order, Instant start, Instant end) {
        return builder.and(
                order.get("status").in(SOLD),
                builder.greaterThanOrEqualTo(order.get("createdAt"), start),
                builder.lessThan(order.get("createdAt"), end));
    }
}
