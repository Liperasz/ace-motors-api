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

    public SavedPaymentMethod () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public PaymentMethod getType() {
        return type;
    }

    public void setType(PaymentMethod type) {
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMaskedData() {
        return maskedData;
    }

    public void setMaskedData(String maskedData) {
        this.maskedData = maskedData;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
