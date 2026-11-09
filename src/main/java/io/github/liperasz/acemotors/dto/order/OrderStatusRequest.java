package io.github.liperasz.acemotors.dto.order;

import io.github.liperasz.acemotors.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Corpo do PUT /orders/{id}/status (adminId virá do JWT no checkpoint 3)
public record OrderStatusRequest(

        @NotNull
        OrderStatus status,

        @NotNull
        Long adminId,

        @NotBlank
        @Size(max = 255)
        String notes
) {
}
