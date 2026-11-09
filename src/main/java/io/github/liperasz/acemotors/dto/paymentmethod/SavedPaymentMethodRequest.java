package io.github.liperasz.acemotors.dto.paymentmethod;

import io.github.liperasz.acemotors.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Cadastro de forma de pagamento.
// data = número do cartão (CREDIT_CARD / DEBIT_CARD) ou chave PIX; só a versão mascarada é salva
public record SavedPaymentMethodRequest(

        @NotNull
        PaymentMethod type,

        @NotBlank
        @Size(max = 50)
        String nickname,

        @NotBlank
        @Size(max = 100)
        String data
) {
}
