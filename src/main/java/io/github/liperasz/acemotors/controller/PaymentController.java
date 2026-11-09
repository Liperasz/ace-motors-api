package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.payment.PaymentRequest;
import io.github.liperasz.acemotors.dto.payment.PaymentResponse;
import io.github.liperasz.acemotors.dto.payment.PaymentStatusHistoryResponse;
import io.github.liperasz.acemotors.entity.Payment;
import io.github.liperasz.acemotors.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController (PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Pagar um pedido -> 201 + Location: /payments/{id}
    @PostMapping("/orders/{orderId}/payments")
    public ResponseEntity<PaymentResponse> pay(@PathVariable Long orderId,
                                               @Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.pay(orderId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/payments/{id}")
                .buildAndExpand(payment.getId())
                .toUri();

        return ResponseEntity.created(location).body(PaymentResponse.from(payment));
    }

    // Tentativas de pagamento de um pedido
    @GetMapping("/orders/{orderId}/payments")
    public Page<PaymentResponse> findByOrder(@PathVariable Long orderId, Pageable pageable) {
        return paymentService.findByOrder(orderId, pageable).map(PaymentResponse::from);
    }

    @GetMapping("/payments/{id}")
    public PaymentResponse findById(@PathVariable Long id) {
        return PaymentResponse.from(paymentService.findById(id));
    }

    // Simula o banco confirmando o PIX
    @PostMapping("/payments/{id}/confirm")
    public PaymentResponse confirmPix(@PathVariable Long id) {
        return PaymentResponse.from(paymentService.confirmPix(id));
    }

    @GetMapping("/payments/{id}/status-history")
    public Page<PaymentStatusHistoryResponse> findStatusHistory(@PathVariable Long id, Pageable pageable) {
        return paymentService.findStatusHistory(id, pageable).map(PaymentStatusHistoryResponse::from);
    }
}
