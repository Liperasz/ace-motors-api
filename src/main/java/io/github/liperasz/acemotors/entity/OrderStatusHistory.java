package io.github.liperasz.acemotors.entity;

import io.github.liperasz.acemotors.enums.OrderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Administrator changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus newStatus;

    @Column(nullable = false)
    private String updateNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant updateTime;
}
