package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Coupon;
import io.github.liperasz.acemotors.repository.coupon.CouponRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryQuery {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
