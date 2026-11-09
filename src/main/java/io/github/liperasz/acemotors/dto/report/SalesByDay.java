package io.github.liperasz.acemotors.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;

// Vendas de um dia: quantidade de pedidos e valor total (já com desconto de cupom)
public record SalesByDay(LocalDate day, Long orders, BigDecimal total) {
}
