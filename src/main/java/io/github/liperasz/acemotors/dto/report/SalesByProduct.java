package io.github.liperasz.acemotors.dto.report;

import java.math.BigDecimal;

// Vendas de um produto: unidades vendidas e valor (soma dos itens, antes do desconto de cupom)
public record SalesByProduct(Long productId, String productName, Long quantity, BigDecimal total) {
}
