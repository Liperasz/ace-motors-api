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

    @ManyToOne(fetch = FetchType.LAZY)
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

    public ProductStatusHistory () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Administrator getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Administrator changedBy) {
        this.changedBy = changedBy;
    }

    public ProductStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ProductStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ProductStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ProductStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getUpdateNotes() {
        return updateNotes;
    }

    public void setUpdateNotes(String updateNotes) {
        this.updateNotes = updateNotes;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }
}
