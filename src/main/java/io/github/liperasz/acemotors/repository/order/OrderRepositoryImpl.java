package io.github.liperasz.acemotors.repository.order;

import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.repository.filter.OrderFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

// Pesquisa de pedidos com Criteria API (filtro por cliente, status e período)
public class OrderRepositoryImpl implements OrderRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Orders> filter(OrderFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Orders> criteria = builder.createQuery(Orders.class);
        Root<Orders> root = criteria.from(Orders.class);

        criteria.where(createRestrictions(filter, builder, root));

        // Sem ordenação informada: pedidos mais recentes primeiro
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");
        criteria.orderBy(QueryUtils.toOrders(sort, root, builder));

        TypedQuery<Orders> query = manager.createQuery(criteria);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Predicate[] createRestrictions(OrderFilter filter, CriteriaBuilder builder, Root<Orders> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getClientId() != null) {
            predicates.add(builder.equal(root.get("client").get("id"), filter.getClientId()));
        }

        if (filter.getStatus() != null) {
            predicates.add(builder.equal(root.get("status"), filter.getStatus()));
        }

        // createdAt é Instant (data + hora); o filtro usa só o dia (LocalDate)
        if (filter.getFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), startOfDay(filter.getFrom())));
        }

        if (filter.getTo() != null) {
            // "até o dia X" = antes do começo do dia X + 1
            predicates.add(builder.lessThan(root.get("createdAt"), startOfDay(filter.getTo().plusDays(1))));
        }

        return predicates.toArray(new Predicate[0]);
    }

    // Primeiro instante do dia, no fuso do servidor
    private Instant startOfDay(LocalDate day) {
        return day.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    private Long total(OrderFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Orders> root = criteria.from(Orders.class);

        criteria.where(createRestrictions(filter, builder, root));
        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }
}
