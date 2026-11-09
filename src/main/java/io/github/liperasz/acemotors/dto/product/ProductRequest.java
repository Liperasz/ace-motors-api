package io.github.liperasz.acemotors.dto.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

// Dados recebidos no POST e no PUT de produto.
// O status não vem aqui: produto novo nasce ACTIVE e muda pelo PUT /products/{id}/status
public record ProductRequest(

        @NotBlank
        @Size(min = 2, max = 150)
        String name,

        @Size(max = 255)
        String description,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotEmpty
        Set<Long> categoryIds
) {
}
