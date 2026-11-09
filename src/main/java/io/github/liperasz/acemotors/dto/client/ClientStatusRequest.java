package io.github.liperasz.acemotors.dto.client;

import io.github.liperasz.acemotors.enums.ClientStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Corpo do PUT /clients/{id}/status (adminId virá do JWT no checkpoint 3)
public record ClientStatusRequest(

        @NotNull
        ClientStatus status,

        @NotNull
        Long adminId,

        @NotBlank
        @Size(max = 255)
        String notes
) {
}
