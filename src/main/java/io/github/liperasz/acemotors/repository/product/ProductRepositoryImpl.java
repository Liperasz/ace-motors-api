package io.github.liperasz.acemotors.repository.product;

import io.github.liperasz.acemotors.dto.product.ProductSummary;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.repository.filter.ProductFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

// Pesquisa de produtos com Criteria API: só aplica os filtros que vierem preenchidos
public class ProductRepositoryImpl implements ProductRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Product> filter(ProductFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<Product> root = criteria.from(Product.class);

        criteria.where(createRestrictions(filter, builder, root));
        criteria.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        TypedQuery<Product> query = manager.createQuery(criteria);
        addPagination(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    @Override
    public Page<ProductSummary> summarize(ProductFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<ProductSummary> criteria = builder.createQuery(ProductSummary.class);
        Root<Product> root = criteria.from(Product.class);

        // Monta o ProductSummary direto na consulta (select só das colunas necessárias)
        criteria.select(builder.construct(ProductSummary.class,
                root.get("id"), root.get("name"), root.get("price"),
                root.get("imageUrl"), root.get("status")));
        criteria.where(createRestrictions(filter, builder, root));
        criteria.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        TypedQuery<ProductSummary> query = manager.createQuery(criteria);
        addPagination(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Predicate[] createRestrictions(ProductFilter filter, CriteriaBuilder builder, Root<Product> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(filter.getName())) {
            predicates.add(builder.like(builder.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"));
        }

        if (filter.getMinPrice() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
        }

        if (filter.getMaxPrice() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
        }

        if (filter.getStatus() != null) {
            predicates.add(builder.equal(root.get("status"), filter.getStatus()));
        }

        // Produto pertence à categoria (join na tabela product_category)
        if (filter.getCategoryId() != null) {
            Join<Product, ?> categories = root.join("categories");
            predicates.add(builder.equal(categories.get("id"), filter.getCategoryId()));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private void addPagination(TypedQuery<?> query, Pageable pageable) {
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
    }

    // Total de registros com os mesmos filtros (necessário para montar a página)
    private Long total(ProductFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Product> root = criteria.from(Product.class);

        criteria.where(createRestrictions(filter, builder, root));
        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }
}
