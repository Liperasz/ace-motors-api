package io.github.liperasz.acemotors.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank
        @Size(max = 150)
        String street,

        @NotBlank
        @Size(max = 10)
        String number,

        @Size(max = 100)
        String complement,

        @NotBlank
        @Size(max = 100)
        String neighborhood,

        @NotBlank
        @Size(max = 100)
        String city,

        // Sigla do estado: SP, PR, RJ...
        @NotBlank
        @Pattern(regexp = "[A-Z]{2}")
        String state,

        // CEP com ou sem hífen: 85000-000 ou 85000000
        @NotBlank
        @Pattern(regexp = "\\d{5}-?\\d{3}")
        String zipCode
) {
}
