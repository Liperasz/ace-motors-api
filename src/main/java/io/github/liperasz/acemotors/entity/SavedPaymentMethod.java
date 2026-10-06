package io.github.liperasz.acemotors.entity;

import io.github.liperasz.acemotors.enums.PaymentMethod;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class SavedPaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod type;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String maskedData;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
