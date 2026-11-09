package io.github.liperasz.acemotors.repository.order;

import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.repository.filter.OrderFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryQuery {

    Page<Orders> filter(OrderFilter filter, Pageable pageable);
}
