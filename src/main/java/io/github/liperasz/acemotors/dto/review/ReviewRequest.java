package io.github.liperasz.acemotors.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(

        // Nota de 1 a 5
        @NotNull
        @Min(1)
        @Max(5)
        Integer rating,

        @Size(max = 255)
        String comment
) {
}
