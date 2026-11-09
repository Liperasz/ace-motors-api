package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.OrderStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    Page<OrderStatusHistory> findByOrderIdOrderByUpdateTimeDesc(Long orderId, Pageable pageable);
}
