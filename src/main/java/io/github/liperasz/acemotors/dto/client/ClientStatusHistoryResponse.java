package io.github.liperasz.acemotors.dto.client;

import io.github.liperasz.acemotors.entity.Administrator;
import io.github.liperasz.acemotors.entity.ClientStatusHistory;
import io.github.liperasz.acemotors.enums.ClientStatus;

import java.time.Instant;

public record ClientStatusHistoryResponse(
        Long id,
        ClientStatus previousStatus,
        ClientStatus newStatus,
        Long changedById,
        String changedByName,
        String notes,
        Instant updateTime
) {

    public static ClientStatusHistoryResponse from(ClientStatusHistory history) {
        Administrator admin = history.getChangedBy();

        return new ClientStatusHistoryResponse(history.getId(), history.getPreviousStatus(),
                history.getNewStatus(),
                admin != null ? admin.getId() : null,
                admin != null ? admin.getName() : null,
                history.getUpdateNotes(), history.getUpdateTime());
    }
}
