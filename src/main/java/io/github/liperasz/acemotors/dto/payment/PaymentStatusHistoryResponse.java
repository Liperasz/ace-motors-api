package io.github.liperasz.acemotors.dto.payment;

import io.github.liperasz.acemotors.entity.Administrator;
import io.github.liperasz.acemotors.entity.PaymentStatusHistory;
import io.github.liperasz.acemotors.enums.PaymentStatus;

import java.time.Instant;

public record PaymentStatusHistoryResponse(
        Long id,
        PaymentStatus previousStatus,
        PaymentStatus newStatus,
        Long changedById,
        String changedByName,
        Instant updateTime
) {

    public static PaymentStatusHistoryResponse from(PaymentStatusHistory history) {
        Administrator admin = history.getChangedBy();

        return new PaymentStatusHistoryResponse(history.getId(), history.getPreviousStatus(),
                history.getNewStatus(),
                admin != null ? admin.getId() : null,
                admin != null ? admin.getName() : null,
                history.getUpdateTime());
    }
}
