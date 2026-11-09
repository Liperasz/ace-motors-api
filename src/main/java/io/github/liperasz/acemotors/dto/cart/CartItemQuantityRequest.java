package io.github.liperasz.acemotors.dto.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Alterar a quantidade de um item (PUT /clients/{id}/cart/items/{itemId})
public record CartItemQuantityRequest(

        @NotNull
        @Positive
        @Max(99)
        Integer quantity
) {
}
