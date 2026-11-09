package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.administrator.AdministratorCreateRequest;
import io.github.liperasz.acemotors.dto.administrator.AdministratorResponse;
import io.github.liperasz.acemotors.dto.administrator.AdministratorUpdateRequest;
import io.github.liperasz.acemotors.entity.Administrator;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.service.AdministratorService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administrators")
public class AdministratorController {

    private final AdministratorService administratorService;

    private final ApplicationEventPublisher publisher;

    public AdministratorController (AdministratorService administratorService, ApplicationEventPublisher publisher) {
        this.administratorService = administratorService;
        this.publisher = publisher;
    }

    @GetMapping
    public Page<AdministratorResponse> findAll(Pageable pageable) {
        return administratorService.findAll(pageable).map(AdministratorResponse::from);
    }

    @GetMapping("/{id}")
    public AdministratorResponse findById(@PathVariable Long id) {
        return AdministratorResponse.from(administratorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdministratorResponse> create(@Valid @RequestBody AdministratorCreateRequest request,
                                                        HttpServletResponse response) {
        Administrator admin = administratorService.create(request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, admin.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(AdministratorResponse.from(admin));
    }

    @PutMapping("/{id}")
    public AdministratorResponse update(@PathVariable Long id,
                                        @Valid @RequestBody AdministratorUpdateRequest request) {
        return AdministratorResponse.from(administratorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        administratorService.delete(id);
    }
}
