package io.github.liperasz.acemotors.dto.paymentmethod;

import io.github.liperasz.acemotors.entity.SavedPaymentMethod;
import io.github.liperasz.acemotors.enums.PaymentMethod;

import java.time.Instant;

public record SavedPaymentMethodResponse(Long id, PaymentMethod type, String nickname, String maskedData,
                                         Instant createdAt) {

    public static SavedPaymentMethodResponse from(SavedPaymentMethod method) {
        return new SavedPaymentMethodResponse(method.getId(), method.getType(), method.getNickname(),
                method.getMaskedData(), method.getCreatedAt());
    }
}
