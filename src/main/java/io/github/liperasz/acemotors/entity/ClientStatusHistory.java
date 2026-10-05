package io.github.liperasz.acemotors.entity;

import io.github.liperasz.acemotors.enums.ClientStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class ClientStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    private Client client;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    private Administrator changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus newStatus;

    @Column(nullable = false)
    private String updateNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant updateTime;

    public ClientStatusHistory () {

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

    public Administrator getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Administrator changedBy) {
        this.changedBy = changedBy;
    }

    public ClientStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ClientStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ClientStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ClientStatus newStatus) {
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


