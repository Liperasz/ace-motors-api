package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.phone.PhoneRequest;
import io.github.liperasz.acemotors.dto.phone.PhoneResponse;
import io.github.liperasz.acemotors.entity.Phone;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.service.PhoneService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/phones")
public class PhoneController {

    private final PhoneService phoneService;

    private final ApplicationEventPublisher publisher;

    public PhoneController (PhoneService phoneService, ApplicationEventPublisher publisher) {
        this.phoneService = phoneService;
        this.publisher = publisher;
    }

    @GetMapping
    public Page<PhoneResponse> findAll(@PathVariable Long clientId, Pageable pageable) {
        return phoneService.findAll(clientId, pageable).map(PhoneResponse::from);
    }

    @GetMapping("/{id}")
    public PhoneResponse findById(@PathVariable Long clientId, @PathVariable Long id) {
        return PhoneResponse.from(phoneService.findById(clientId, id));
    }

    @PostMapping
    public ResponseEntity<PhoneResponse> create(@PathVariable Long clientId,
                                                @Valid @RequestBody PhoneRequest request,
                                                HttpServletResponse response) {
        Phone phone = phoneService.create(clientId, request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, phone.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(PhoneResponse.from(phone));
    }

    @PutMapping("/{id}")
    public PhoneResponse update(@PathVariable Long clientId, @PathVariable Long id,
                                @Valid @RequestBody PhoneRequest request) {
        return PhoneResponse.from(phoneService.update(clientId, id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long clientId, @PathVariable Long id) {
        phoneService.delete(clientId, id);
    }
}
