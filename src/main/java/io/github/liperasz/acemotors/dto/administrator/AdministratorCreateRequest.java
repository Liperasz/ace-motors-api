package io.github.liperasz.acemotors.dto.administrator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdministratorCreateRequest(

        @NotBlank
        @Size(min = 2, max = 150)
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password
) {
}
