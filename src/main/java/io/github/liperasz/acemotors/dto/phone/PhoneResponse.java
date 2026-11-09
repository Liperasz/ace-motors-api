package io.github.liperasz.acemotors.dto.phone;

import io.github.liperasz.acemotors.entity.Phone;

import java.time.Instant;

public record PhoneResponse(Long id, String number, Instant createdAt) {

    public static PhoneResponse from(Phone phone) {
        return new PhoneResponse(phone.getId(), phone.getNumber(), phone.getCreatedAt());
    }
}
