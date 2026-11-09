package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.order.CheckoutRequest;
import io.github.liperasz.acemotors.dto.order.OrderResponse;
import io.github.liperasz.acemotors.dto.order.OrderSummary;
import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.repository.filter.OrderFilter;
import io.github.liperasz.acemotors.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// Pedidos vistos pelo cliente: checkout e histórico de compras
@RestController
@RequestMapping("/clients/{clientId}/orders")
public class ClientOrderController {

    private final OrderService orderService;

    public ClientOrderController (OrderService orderService) {
        this.orderService = orderService;
    }

    // Histórico de pedidos do cliente: GET /clients/2/orders?status=&from=&to=
    @GetMapping
    public Page<OrderSummary> findAll(@PathVariable Long clientId, OrderFilter filter, Pageable pageable) {
        return orderService.findByClient(clientId, filter, pageable).map(OrderSummary::from);
    }

    // Checkout. O Location aponta para /orders/{id} (onde o pedido é consultado),
    // por isso aqui não dá para usar o ResourceCreatedEvent, que monta a URL a partir da atual
    @PostMapping
    public ResponseEntity<OrderResponse> checkout(@PathVariable Long clientId,
                                                  @Valid @RequestBody CheckoutRequest request) {
        Orders order = orderService.checkout(clientId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/orders/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity.created(location).body(OrderResponse.from(order));
    }
}
