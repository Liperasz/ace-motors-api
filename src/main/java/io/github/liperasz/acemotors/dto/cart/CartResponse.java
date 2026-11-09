package io.github.liperasz.acemotors.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Long id, List<CartItemResponse> items, Integer totalQuantity, BigDecimal total) {

    public static CartResponse from(Long cartId, List<CartItemResponse> items) {
        int totalQuantity = items.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cartId, items, totalQuantity, total);
    }
}
