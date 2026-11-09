package io.github.liperasz.acemotors.repository.client;

import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.repository.filter.ClientFilter;
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

// Pesquisa de clientes com Criteria API (mesmo modelo do ProductRepositoryImpl)
public class ClientRepositoryImpl implements ClientRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Client> filter(ClientFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Client> criteria = builder.createQuery(Client.class);
        Root<Client> root = criteria.from(Client.class);

        criteria.where(createRestrictions(filter, builder, root));
        criteria.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        TypedQuery<Client> query = manager.createQuery(criteria);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Predicate[] createRestrictions(ClientFilter filter, CriteriaBuilder builder, Root<Client> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(filter.getName())) {
            predicates.add(builder.like(builder.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"));
        }

        if (StringUtils.hasText(filter.getEmail())) {
            predicates.add(builder.like(builder.lower(root.get("email")),
                    "%" + filter.getEmail().toLowerCase() + "%"));
        }

        if (filter.getStatus() != null) {
            predicates.add(builder.equal(root.get("status"), filter.getStatus()));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private Long total(ClientFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Client> root = criteria.from(Client.class);

        criteria.where(createRestrictions(filter, builder, root));
        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }
}
