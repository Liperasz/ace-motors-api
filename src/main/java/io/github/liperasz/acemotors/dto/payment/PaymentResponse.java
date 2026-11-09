package io.github.liperasz.acemotors.dto.payment;

import io.github.liperasz.acemotors.entity.Payment;
import io.github.liperasz.acemotors.enums.PaymentMethod;
import io.github.liperasz.acemotors.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

// transactionCode: no PIX é o "copia e cola" fictício; no cartão é o código da transação simulada
public record PaymentResponse(
        Long id,
        Long orderId,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal amount,
        String transactionCode,
        Long savedPaymentMethodId,
        Instant createdAt,
        Instant updatedAt
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getOrder().getId(), payment.getMethod(),
                payment.getStatus(), payment.getAmount(), payment.getTransactionCode(),
                payment.getPaymentMethod() != null ? payment.getPaymentMethod().getId() : null,
                payment.getCreatedAt(), payment.getUpdatedAt());
    }
}
