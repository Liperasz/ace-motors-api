package io.github.liperasz.acemotors.entity;

import io.github.liperasz.acemotors.enums.ClientStatus;
import jakarta.persistence.*;

@Entity
public class Client extends Person{

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus status = ClientStatus.ACTIVE;

    public Client () {

    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }
}
