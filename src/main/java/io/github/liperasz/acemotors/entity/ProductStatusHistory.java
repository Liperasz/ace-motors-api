package io.github.liperasz.acemotors.entity;

import io.github.liperasz.acemotors.enums.ProductStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class ProductStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Administrator changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus newStatus;

    @Column(nullable = false)
    private String updateNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant updateTime;
}
