package io.github.liperasz.acemotors.dto.favorite;

import io.github.liperasz.acemotors.dto.product.ProductSummary;
import io.github.liperasz.acemotors.entity.Favorite;
import io.github.liperasz.acemotors.entity.Product;

import java.time.Instant;

public record FavoriteResponse(Long id, ProductSummary product, Instant createdAt) {

    public static FavoriteResponse from(Favorite favorite) {
        Product p = favorite.getProduct();
        ProductSummary product = new ProductSummary(p.getId(), p.getName(), p.getPrice(), p.getImageUrl(),
                p.getStatus());

        return new FavoriteResponse(favorite.getId(), product, favorite.getCreatedAt());
    }
}
