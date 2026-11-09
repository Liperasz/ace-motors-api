package io.github.liperasz.acemotors.dto.coupon;

import io.github.liperasz.acemotors.enums.CouponType;

import java.math.BigDecimal;

// Resultado de GET /coupons/validate: quanto o cupom desconta no subtotal informado
public record CouponValidationResponse(
        String code,
        CouponType type,
        BigDecimal value,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal total
) {
}
