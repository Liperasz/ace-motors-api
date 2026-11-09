package io.github.liperasz.acemotors.dto.order;

import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// Pedido completo: itens com o preço da época e o endereço copiado no momento da compra
public record OrderResponse(
        Long id,
        Long clientId,
        String clientName,
        OrderStatus status,
        String couponCode,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal total,
        String deliveryAddress,
        List<OrderItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {

    public static OrderResponse from(Orders order) {
        return new OrderResponse(order.getId(), order.getClient().getId(), order.getClient().getName(),
                order.getStatus(),
                order.getCoupon() != null ? order.getCoupon().getCode() : null,
                order.getSubtotal(), order.getDiscount(), order.getTotal(),
                order.getSnapshot() != null ? order.getSnapshot().getAddressSnapshot() : null,
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getCreatedAt(), order.getUpdatedAt());
    }
}
