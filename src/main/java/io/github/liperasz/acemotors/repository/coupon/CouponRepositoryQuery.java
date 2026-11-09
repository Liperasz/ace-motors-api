package io.github.liperasz.acemotors.repository.coupon;

import io.github.liperasz.acemotors.entity.Coupon;
import io.github.liperasz.acemotors.repository.filter.CouponFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryQuery {

    Page<Coupon> filter(CouponFilter filter, Pageable pageable);
}
