package io.github.liperasz.acemotors.dto.cart;

import io.github.liperasz.acemotors.entity.CartItem;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.enums.ProductStatus;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String imageUrl,
        ProductStatus productStatus,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
) {

    public static CartItemResponse from(CartItem item) {
        Product product = item.getProduct();
        // Preço atual do produto: no carrinho ele ainda pode mudar (só congela ao virar pedido)
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(item.getId(), product.getId(), product.getName(), product.getImageUrl(),
                product.getStatus(), product.getPrice(), item.getQuantity(), subtotal);
    }
}
