package io.github.liperasz.acemotors.entity;

import io.github.liperasz.acemotors.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class PaymentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Administrator changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus newStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant updateTime;
}
