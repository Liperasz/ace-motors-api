package io.github.liperasz.acemotors.dto.paymentmethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Só o apelido pode ser alterado; para trocar o cartão/chave, cadastra-se outro
public record SavedPaymentMethodUpdateRequest(

        @NotBlank
        @Size(max = 50)
        String nickname
) {
}
