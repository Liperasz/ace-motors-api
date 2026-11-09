package io.github.liperasz.acemotors.dto.phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PhoneRequest(

        // DDD + número, só dígitos: 10 (fixo) ou 11 (celular)
        @NotBlank
        @Pattern(regexp = "\\d{10,11}")
        String number
) {
}
