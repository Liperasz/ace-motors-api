package io.github.liperasz.acemotors.dto.product;

import io.github.liperasz.acemotors.dto.category.CategoryResponse;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        ProductStatus status,
        List<CategoryResponse> categories,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProductResponse from(Product product) {
        List<CategoryResponse> categories = product.getCategories().stream()
                .map(CategoryResponse::from)
                .sorted(Comparator.comparing(CategoryResponse::name))
                .toList();

        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getImageUrl(), product.getStatus(), categories,
                product.getCreatedAt(), product.getUpdatedAt());
    }
}
