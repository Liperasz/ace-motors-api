package io.github.liperasz.acemotors.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;

    @Column(nullable = false)
    private String number;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
