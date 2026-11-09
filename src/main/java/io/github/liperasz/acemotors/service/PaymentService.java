package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.payment.PaymentRequest;
import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.entity.Payment;
import io.github.liperasz.acemotors.entity.PaymentStatusHistory;
import io.github.liperasz.acemotors.entity.SavedPaymentMethod;
import io.github.liperasz.acemotors.enums.OrderStatus;
import io.github.liperasz.acemotors.enums.PaymentMethod;
import io.github.liperasz.acemotors.enums.PaymentStatus;
import io.github.liperasz.acemotors.repository.PaymentRepository;
import io.github.liperasz.acemotors.repository.PaymentStatusHistoryRepository;
import io.github.liperasz.acemotors.repository.SavedPaymentMethodRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import io.github.liperasz.acemotors.util.CardUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// Pagamentos simulados (RF12): nenhuma comunicação com banco/operadora real
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentStatusHistoryRepository historyRepository;

    private final OrderService orderService;

    private final ClientService clientService;

    private final SavedPaymentMethodRepository savedPaymentMethodRepository;

    public PaymentService (PaymentRepository paymentRepository, PaymentStatusHistoryRepository historyRepository,
                           OrderService orderService, ClientService clientService,
                           SavedPaymentMethodRepository savedPaymentMethodRepository) {
        this.paymentRepository = paymentRepository;
        this.historyRepository = historyRepository;
        this.orderService = orderService;
        this.clientService = clientService;
        this.savedPaymentMethodRepository = savedPaymentMethodRepository;
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento", id));
    }

    public Page<Payment> findByOrder(Long orderId, Pageable pageable) {
        orderService.findById(orderId);
        return paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId, pageable);
    }

    public Page<PaymentStatusHistory> findStatusHistory(Long id, Pageable pageable) {
        findById(id);
        return historyRepository.findByPaymentIdOrderByUpdateTimeDesc(id, pageable);
    }

    // Cria uma tentativa de pagamento para o pedido.
    // PIX: fica PENDING até ser confirmado. Cartão: já sai APPROVED (número válido) ou DECLINED
    @Transactional
    public Payment pay(Long orderId, PaymentRequest request) {
        Orders order = orderService.findById(orderId);
        clientService.findActive(order.getClient().getId());

        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            throw new BusinessException("payment.order-not-awaiting",
                    "Pedido " + orderId + " está com status " + order.getStatus());
        }

        // Um PIX pendente precisa ser confirmado antes de outra tentativa
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.PENDING)) {
            throw new BusinessException("payment.pending-exists", "Pedido " + orderId + " já tem pagamento pendente");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(request.method());
        payment.setAmount(order.getTotal());

        if (request.savedPaymentMethodId() != null) {
            // A forma salva precisa ser do dono do pedido
            SavedPaymentMethod saved = savedPaymentMethodRepository
                    .findByIdAndClientId(request.savedPaymentMethodId(), order.getClient().getId())
                    .orElseThrow(() -> new BusinessException("payment.saved-method-invalid",
                            "Forma de pagamento " + request.savedPaymentMethodId() + " não é do cliente do pedido"));

            if (saved.getType() != request.method()) {
                throw new BusinessException("payment.method-mismatch",
                        "Forma salva é " + saved.getType() + ", mas o pagamento é " + request.method());
            }
            payment.setPaymentMethod(saved);
        }

        if (request.method() == PaymentMethod.PIX) {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionCode(fakePixCode(order));
        } else {
            payment.setStatus(simulateCard(request) ? PaymentStatus.APPROVED : PaymentStatus.DECLINED);
            payment.setTransactionCode("CARD-" + UUID.randomUUID());
        }

        paymentRepository.save(payment);
        saveHistory(payment, null, payment.getStatus());

        if (payment.getStatus() == PaymentStatus.APPROVED) {
            orderService.changeStatus(order, OrderStatus.PAID, null, "Pagamento aprovado (cartão)");
        }

        return payment;
    }

    // Simula a confirmação do PIX pelo banco (na vida real seria um aviso/webhook do banco)
    @Transactional
    public Payment confirmPix(Long id) {
        Payment payment = findById(id);

        if (payment.getMethod() != PaymentMethod.PIX || payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("payment.not-pending-pix", "Pagamento " + id + " não é um PIX pendente");
        }

        saveHistory(payment, PaymentStatus.PENDING, PaymentStatus.APPROVED);
        payment.setStatus(PaymentStatus.APPROVED);
        paymentRepository.save(payment);

        orderService.changeStatus(payment.getOrder(), OrderStatus.PAID, null, "Pagamento aprovado (PIX)");
        return payment;
    }

    // Cartão salvo: considerado válido (foi validado no cadastro).
    // Cartão digitado: obrigatório e aprovado só se passar no algoritmo de Luhn
    private boolean simulateCard(PaymentRequest request) {
        if (request.savedPaymentMethodId() != null) {
            return true;
        }

        if (request.cardNumber() == null || request.cardNumber().isBlank()) {
            throw new BusinessException("payment.card-required", "Informe cardNumber ou savedPaymentMethodId");
        }

        return CardUtils.isValidCardNumber(request.cardNumber());
    }

    // Código "copia e cola" fictício (só imita o formato de um PIX real)
    private String fakePixCode(Orders order) {
        return "00020126580014BR.GOV.BCB.PIX0136" + UUID.randomUUID()
                + "5204000053039865406" + order.getTotal() + "5802BR5910ACE MOTORS6009CASCAVEL6304ABCD";
    }

    private void saveHistory(Payment payment, PaymentStatus previous, PaymentStatus next) {
        PaymentStatusHistory history = new PaymentStatusHistory();
        history.setPayment(payment);
        history.setPreviousStatus(previous);
        history.setNewStatus(next);
        historyRepository.save(history);
    }
}
