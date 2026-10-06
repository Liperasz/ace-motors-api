package io.github.liperasz.acemotors.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class OrderSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Orders order;

    @Column(nullable = false)
    private String addressSnapshot;

    @Column(nullable = false)
    private BigDecimal totalSnapshot;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant snapshotDate;
}
