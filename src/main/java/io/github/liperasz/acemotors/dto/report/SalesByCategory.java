package io.github.liperasz.acemotors.dto.report;

import java.math.BigDecimal;

// Vendas de uma categoria. Produto com várias categorias conta em cada uma delas
public record SalesByCategory(Long categoryId, String categoryName, Long quantity, BigDecimal total) {
}
