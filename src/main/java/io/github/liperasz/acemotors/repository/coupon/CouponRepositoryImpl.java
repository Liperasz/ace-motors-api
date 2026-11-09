package io.github.liperasz.acemotors.repository.coupon;

import io.github.liperasz.acemotors.entity.Coupon;
import io.github.liperasz.acemotors.repository.filter.CouponFilter;
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
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

// Pesquisa de cupons com Criteria API, incluindo filtro por data (LocalDate)
public class CouponRepositoryImpl implements CouponRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Coupon> filter(CouponFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Coupon> criteria = builder.createQuery(Coupon.class);
        Root<Coupon> root = criteria.from(Coupon.class);

        criteria.where(createRestrictions(filter, builder, root));
        criteria.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        TypedQuery<Coupon> query = manager.createQuery(criteria);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Predicate[] createRestrictions(CouponFilter filter, CriteriaBuilder builder, Root<Coupon> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(filter.getCode())) {
            predicates.add(builder.like(builder.upper(root.get("code")),
                    "%" + filter.getCode().toUpperCase() + "%"));
        }

        if (filter.getActive() != null) {
            predicates.add(builder.equal(root.get("active"), filter.getActive()));
        }

        // startDate <= validOn <= expirationDate
        if (filter.getValidOn() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("startDate"), filter.getValidOn()));
            predicates.add(builder.greaterThanOrEqualTo(root.get("expirationDate"), filter.getValidOn()));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private Long total(CouponFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Coupon> root = criteria.from(Coupon.class);

        criteria.where(createRestrictions(filter, builder, root));
        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }
}
