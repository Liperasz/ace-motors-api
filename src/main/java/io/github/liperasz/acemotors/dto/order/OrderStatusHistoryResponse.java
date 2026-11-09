package io.github.liperasz.acemotors.dto.order;

import io.github.liperasz.acemotors.entity.Administrator;
import io.github.liperasz.acemotors.entity.OrderStatusHistory;
import io.github.liperasz.acemotors.enums.OrderStatus;

import java.time.Instant;

public record OrderStatusHistoryResponse(
        Long id,
        OrderStatus previousStatus,
        OrderStatus newStatus,
        Long changedById,
        String changedByName,
        String notes,
        Instant updateTime
) {

    public static OrderStatusHistoryResponse from(OrderStatusHistory history) {
        // changedBy nulo = mudança automática (criação do pedido, pagamento aprovado...)
        Administrator admin = history.getChangedBy();

        return new OrderStatusHistoryResponse(history.getId(), history.getPreviousStatus(),
                history.getNewStatus(),
                admin != null ? admin.getId() : null,
                admin != null ? admin.getName() : null,
                history.getUpdateNotes(), history.getUpdateTime());
    }
}
