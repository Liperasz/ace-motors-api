package io.github.liperasz.acemotors.dto.payment;

import io.github.liperasz.acemotors.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Pagamento de um pedido (POST /orders/{orderId}/payments).
// Cartão: informar cardNumber OU savedPaymentMethodId. PIX: nenhum dos dois é necessário
public record PaymentRequest(

        @NotNull
        PaymentMethod method,

        @Size(max = 30)
        String cardNumber,

        Long savedPaymentMethodId
) {
}
