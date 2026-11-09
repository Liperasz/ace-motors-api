package io.github.liperasz.acemotors.dto.product;

import io.github.liperasz.acemotors.enums.ProductStatus;

import java.math.BigDecimal;

// Versão resumida do produto, para a listagem do catálogo (GET /products?summary)
public record ProductSummary(Long id, String name, BigDecimal price, String imageUrl, ProductStatus status) {
}
