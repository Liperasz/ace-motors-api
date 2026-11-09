package io.github.liperasz.acemotors.dto.favorite;

import jakarta.validation.constraints.NotNull;

public record FavoriteRequest(

        @NotNull
        Long productId
) {
}
