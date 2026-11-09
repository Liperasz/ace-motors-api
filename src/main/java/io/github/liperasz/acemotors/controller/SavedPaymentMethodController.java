package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.paymentmethod.SavedPaymentMethodRequest;
import io.github.liperasz.acemotors.dto.paymentmethod.SavedPaymentMethodResponse;
import io.github.liperasz.acemotors.dto.paymentmethod.SavedPaymentMethodUpdateRequest;
import io.github.liperasz.acemotors.entity.SavedPaymentMethod;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.service.SavedPaymentMethodService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/payment-methods")
public class SavedPaymentMethodController {

    private final SavedPaymentMethodService service;

    private final ApplicationEventPublisher publisher;

    public SavedPaymentMethodController (SavedPaymentMethodService service, ApplicationEventPublisher publisher) {
        this.service = service;
        this.publisher = publisher;
    }

    @GetMapping
    public Page<SavedPaymentMethodResponse> findAll(@PathVariable Long clientId, Pageable pageable) {
        return service.findAll(clientId, pageable).map(SavedPaymentMethodResponse::from);
    }

    @GetMapping("/{id}")
    public SavedPaymentMethodResponse findById(@PathVariable Long clientId, @PathVariable Long id) {
        return SavedPaymentMethodResponse.from(service.findById(clientId, id));
    }

    @PostMapping
    public ResponseEntity<SavedPaymentMethodResponse> create(@PathVariable Long clientId,
                                                             @Valid @RequestBody SavedPaymentMethodRequest request,
                                                             HttpServletResponse response) {
        SavedPaymentMethod method = service.create(clientId, request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, method.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(SavedPaymentMethodResponse.from(method));
    }

    @PutMapping("/{id}")
    public SavedPaymentMethodResponse update(@PathVariable Long clientId, @PathVariable Long id,
                                             @Valid @RequestBody SavedPaymentMethodUpdateRequest request) {
        return SavedPaymentMethodResponse.from(service.update(clientId, id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long clientId, @PathVariable Long id) {
        service.delete(clientId, id);
    }
}
