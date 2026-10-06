package io.github.liperasz.acemotors.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
