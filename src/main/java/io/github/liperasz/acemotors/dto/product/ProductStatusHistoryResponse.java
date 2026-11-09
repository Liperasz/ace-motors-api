package io.github.liperasz.acemotors.dto.product;

import io.github.liperasz.acemotors.entity.Administrator;
import io.github.liperasz.acemotors.entity.ProductStatusHistory;
import io.github.liperasz.acemotors.enums.ProductStatus;

import java.time.Instant;

public record ProductStatusHistoryResponse(
        Long id,
        ProductStatus previousStatus,
        ProductStatus newStatus,
        Long changedById,
        String changedByName,
        String notes,
        Instant updateTime
) {

    public static ProductStatusHistoryResponse from(ProductStatusHistory history) {
        // changedBy pode ser nulo quando a mudança foi automática
        Administrator admin = history.getChangedBy();

        return new ProductStatusHistoryResponse(history.getId(), history.getPreviousStatus(),
                history.getNewStatus(),
                admin != null ? admin.getId() : null,
                admin != null ? admin.getName() : null,
                history.getUpdateNotes(), history.getUpdateTime());
    }
}
