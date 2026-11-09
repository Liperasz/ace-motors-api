package io.github.liperasz.acemotors.dto.client;

import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.enums.ClientStatus;

import java.time.Instant;

// A senha nunca é devolvida
public record ClientResponse(Long id, String name, String email, ClientStatus status,
                             Instant createdAt, Instant updatedAt) {

    public static ClientResponse from(Client client) {
        return new ClientResponse(client.getId(), client.getName(), client.getEmail(), client.getStatus(),
                client.getCreatedAt(), client.getUpdatedAt());
    }
}
