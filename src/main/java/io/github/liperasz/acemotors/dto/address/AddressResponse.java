package io.github.liperasz.acemotors.dto.address;

import io.github.liperasz.acemotors.entity.Address;

import java.time.Instant;

public record AddressResponse(Long id, String street, String number, String complement, String neighborhood,
                              String city, String state, String zipCode, Instant createdAt) {

    public static AddressResponse from(Address address) {
        return new AddressResponse(address.getId(), address.getStreet(), address.getNumber(),
                address.getComplement(), address.getNeighborhood(), address.getCity(), address.getState(),
                address.getZipCode(), address.getCreatedAt());
    }
}
