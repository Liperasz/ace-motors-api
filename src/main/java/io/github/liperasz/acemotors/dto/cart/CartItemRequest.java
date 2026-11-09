package io.github.liperasz.acemotors.dto.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Adicionar produto ao carrinho (POST /clients/{id}/cart/items)
public record CartItemRequest(

        @NotNull
        Long productId,

        @NotNull
        @Positive
        @Max(99)
        Integer quantity
) {
}
