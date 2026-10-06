package io.github.liperasz.acemotors.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "product_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
