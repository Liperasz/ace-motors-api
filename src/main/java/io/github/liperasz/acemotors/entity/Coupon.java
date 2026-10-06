package io.github.liperasz.acemotors.entity;

import io.github.liperasz.acemotors.enums.CouponType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant expirationDate;

    @Column(nullable = false)
    private Integer maxUsage;

    @Column(nullable = false)
    private Integer currentUsage = 0;

    @Column(nullable = false)
    private Boolean active = true;
}
