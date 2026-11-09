package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
