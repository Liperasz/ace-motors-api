package io.github.liperasz.acemotors.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Atualização dos dados do cliente (PUT /clients/{id}). A senha não muda por aqui
public record ClientUpdateRequest(

        @NotBlank
        @Size(min = 2, max = 150)
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        String email
) {
}
