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

    @ManyToOne(fetch = FetchType.LAZY)
    private Administrator changedBy;

    @Enumerated(EnumType.STRING)
    private PaymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus newStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant updateTime;

    public PaymentStatusHistory () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Administrator getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Administrator changedBy) {
        this.changedBy = changedBy;
    }

    public PaymentStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(PaymentStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public PaymentStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(PaymentStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }
}
