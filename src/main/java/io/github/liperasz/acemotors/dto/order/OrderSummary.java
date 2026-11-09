package io.github.liperasz.acemotors.dto.order;

import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

// Versão resumida para as listagens de pedidos
public record OrderSummary(Long id, Long clientId, String clientName, OrderStatus status, BigDecimal total,
                           Instant createdAt) {

    public static OrderSummary from(Orders order) {
        return new OrderSummary(order.getId(), order.getClient().getId(), order.getClient().getName(),
                order.getStatus(), order.getTotal(), order.getCreatedAt());
    }
}
