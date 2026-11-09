package io.github.liperasz.acemotors.dto.coupon;

import io.github.liperasz.acemotors.entity.Coupon;
import io.github.liperasz.acemotors.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CouponResponse(
        Long id,
        String code,
        CouponType type,
        BigDecimal value,
        LocalDate startDate,
        LocalDate expirationDate,
        Integer maxUsage,
        Integer currentUsage,
        Boolean active
) {

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(coupon.getId(), coupon.getCode(), coupon.getType(), coupon.getValue(),
                coupon.getStartDate(), coupon.getExpirationDate(), coupon.getMaxUsage(),
                coupon.getCurrentUsage(), coupon.getActive());
    }
}
