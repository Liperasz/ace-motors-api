package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.address.AddressRequest;
import io.github.liperasz.acemotors.dto.address.AddressResponse;
import io.github.liperasz.acemotors.entity.Address;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.service.AddressService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/addresses")
public class AddressController {

    private final AddressService addressService;

    private final ApplicationEventPublisher publisher;

    public AddressController (AddressService addressService, ApplicationEventPublisher publisher) {
        this.addressService = addressService;
        this.publisher = publisher;
    }

    @GetMapping
    public Page<AddressResponse> findAll(@PathVariable Long clientId, Pageable pageable) {
        return addressService.findAll(clientId, pageable).map(AddressResponse::from);
    }

    @GetMapping("/{id}")
    public AddressResponse findById(@PathVariable Long clientId, @PathVariable Long id) {
        return AddressResponse.from(addressService.findById(clientId, id));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(@PathVariable Long clientId,
                                                  @Valid @RequestBody AddressRequest request,
                                                  HttpServletResponse response) {
        Address address = addressService.create(clientId, request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, address.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(AddressResponse.from(address));
    }

    @PutMapping("/{id}")
    public AddressResponse update(@PathVariable Long clientId, @PathVariable Long id,
                                  @Valid @RequestBody AddressRequest request) {
        return AddressResponse.from(addressService.update(clientId, id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long clientId, @PathVariable Long id) {
        addressService.delete(clientId, id);
    }
}
