package io.github.liperasz.acemotors.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Dados recebidos no POST e no PUT de categoria
public record CategoryRequest(

        @NotBlank
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 255)
        String description
) {
}
