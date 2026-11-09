package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.order.OrderResponse;
import io.github.liperasz.acemotors.dto.order.OrderStatusHistoryResponse;
import io.github.liperasz.acemotors.dto.order.OrderStatusRequest;
import io.github.liperasz.acemotors.dto.order.OrderSummary;
import io.github.liperasz.acemotors.repository.filter.OrderFilter;
import io.github.liperasz.acemotors.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

// Visão geral dos pedidos (área administrativa)
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController (OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /orders?clientId=&status=&from=2026-11-01&to=2026-11-30&page=&size=&sort=
    @GetMapping
    public Page<OrderSummary> filter(OrderFilter filter, Pageable pageable) {
        return orderService.filter(filter, pageable).map(OrderSummary::from);
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return OrderResponse.from(orderService.findById(id));
    }

    // Atualização parcial: só o status, respeitando as transições permitidas
    @PutMapping("/{id}/status")
    public OrderResponse changeStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request) {
        return OrderResponse.from(orderService.changeStatus(id, request));
    }

    @GetMapping("/{id}/status-history")
    public Page<OrderStatusHistoryResponse> findStatusHistory(@PathVariable Long id, Pageable pageable) {
        return orderService.findStatusHistory(id, pageable).map(OrderStatusHistoryResponse::from);
    }
}
