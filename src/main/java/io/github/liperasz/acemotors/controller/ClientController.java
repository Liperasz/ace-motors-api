package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.client.*;
import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.repository.filter.ClientFilter;
import io.github.liperasz.acemotors.service.ClientService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    private final ApplicationEventPublisher publisher;

    public ClientController (ClientService clientService, ApplicationEventPublisher publisher) {
        this.clientService = clientService;
        this.publisher = publisher;
    }

    // GET /clients?name=&email=&status=&page=&size=&sort=
    @GetMapping
    public Page<ClientResponse> filter(ClientFilter filter, Pageable pageable) {
        return clientService.filter(filter, pageable).map(ClientResponse::from);
    }

    @GetMapping("/{id}")
    public ClientResponse findById(@PathVariable Long id) {
        return ClientResponse.from(clientService.findById(id));
    }

    // Cadastro de cliente (o login com JWT entra no checkpoint 3)
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientCreateRequest request,
                                                 HttpServletResponse response) {
        Client client = clientService.create(request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, client.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.from(client));
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request) {
        return ClientResponse.from(clientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }

    // Atualização parcial: só o status (ACTIVE / INACTIVE)
    @PutMapping("/{id}/status")
    public ClientResponse changeStatus(@PathVariable Long id, @Valid @RequestBody ClientStatusRequest request) {
        return ClientResponse.from(clientService.changeStatus(id, request));
    }

    @GetMapping("/{id}/status-history")
    public Page<ClientStatusHistoryResponse> findStatusHistory(@PathVariable Long id, Pageable pageable) {
        return clientService.findStatusHistory(id, pageable).map(ClientStatusHistoryResponse::from);
    }
}
