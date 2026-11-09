package io.github.liperasz.acemotors.dto.product;

import io.github.liperasz.acemotors.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Corpo do PUT /products/{id}/status.
// adminId vem no corpo por enquanto; no checkpoint 3 virá do usuário logado (JWT)
public record ProductStatusRequest(

        @NotNull
        ProductStatus status,

        @NotNull
        Long adminId,

        @NotBlank
        @Size(max = 255)
        String notes
) {
}
