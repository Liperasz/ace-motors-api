package io.github.liperasz.acemotors.dto.coupon;

import io.github.liperasz.acemotors.enums.CouponType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// Datas no formato yyyy-MM-dd (ex: "2026-12-31")
public record CouponRequest(

        // Letras, números, "-" e "_" (salvo sempre em maiúsculas)
        @NotBlank
        @Pattern(regexp = "[A-Za-z0-9_-]{3,30}")
        String code,

        @NotNull
        CouponType type,

        // PERCENTAGE: 10 = 10%; FIXED_VALUE: 10 = R$ 10,00
        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal value,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate expirationDate,

        @NotNull
        @Positive
        Integer maxUsage
) {
}
