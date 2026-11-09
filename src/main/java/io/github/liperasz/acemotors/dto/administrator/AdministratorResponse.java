package io.github.liperasz.acemotors.dto.administrator;

import io.github.liperasz.acemotors.entity.Administrator;

import java.time.Instant;

public record AdministratorResponse(Long id, String name, String email, Instant createdAt, Instant updatedAt) {

    public static AdministratorResponse from(Administrator admin) {
        return new AdministratorResponse(admin.getId(), admin.getName(), admin.getEmail(),
                admin.getCreatedAt(), admin.getUpdatedAt());
    }
}
