package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.order.CheckoutRequest;
import io.github.liperasz.acemotors.dto.order.OrderStatusRequest;
import io.github.liperasz.acemotors.entity.*;
import io.github.liperasz.acemotors.enums.OrderStatus;
import io.github.liperasz.acemotors.enums.PaymentStatus;
import io.github.liperasz.acemotors.repository.AddressRepository;
import io.github.liperasz.acemotors.repository.CartItemRepository;
import io.github.liperasz.acemotors.repository.CartRepository;
import io.github.liperasz.acemotors.repository.OrderRepository;
import io.github.liperasz.acemotors.repository.OrderStatusHistoryRepository;
import io.github.liperasz.acemotors.repository.PaymentRepository;
import io.github.liperasz.acemotors.repository.PaymentStatusHistoryRepository;
import io.github.liperasz.acemotors.repository.filter.OrderFilter;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderService {

    // Para quais status cada status pode ir
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.AWAITING_PAYMENT, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    private final OrderRepository orderRepository;

    private final OrderStatusHistoryRepository historyRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final AddressRepository addressRepository;

    private final ClientService clientService;

    private final ProductService productService;

    private final CouponService couponService;

    private final AdministratorService administratorService;

    private final PaymentRepository paymentRepository;

    private final PaymentStatusHistoryRepository paymentHistoryRepository;

    public OrderService (OrderRepository orderRepository, OrderStatusHistoryRepository historyRepository,
                         CartRepository cartRepository, CartItemRepository cartItemRepository,
                         AddressRepository addressRepository, ClientService clientService,
                         ProductService productService, CouponService couponService,
                         AdministratorService administratorService, PaymentRepository paymentRepository,
                         PaymentStatusHistoryRepository paymentHistoryRepository) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.clientService = clientService;
        this.productService = productService;
        this.couponService = couponService;
        this.administratorService = administratorService;
        this.paymentRepository = paymentRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public Page<Orders> filter(OrderFilter filter, Pageable pageable) {
        return orderRepository.filter(filter, pageable);
    }

    // Histórico de pedidos de um cliente (mesma pesquisa, com o cliente fixo)
    public Page<Orders> findByClient(Long clientId, OrderFilter filter, Pageable pageable) {
        clientService.findById(clientId);
        filter.setClientId(clientId);
        return orderRepository.filter(filter, pageable);
    }

    public Orders findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    // Finalização da compra: transforma itens do carrinho em um pedido.
    // @Transactional: se qualquer regra falhar, nada é salvo (nem pedido, nem uso do cupom, nem remoção do carrinho)
    @Transactional
    public Orders checkout(Long clientId, CheckoutRequest request) {
        Client client = clientService.findActive(clientId);

        Address address = addressRepository.findByIdAndPersonIdAndActiveTrue(request.addressId(), clientId)
                .orElseThrow(() -> new BusinessException("order.address-invalid",
                        "Endereço " + request.addressId() + " não pertence ao cliente ou está inativo"));

        List<CartItem> cartItems = findItemsToBuy(clientId, request.cartItemIds());

        Orders order = new Orders();
        order.setClient(client);
        order.setAddress(address);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);

        // Cada item guarda o preço do momento da compra
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = productService.findAvailable(cartItem.getProduct().getId());
            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(itemSubtotal);
            order.getItems().add(item);

            subtotal = subtotal.add(itemSubtotal);
        }

        // Cupom (opcional): valida, calcula o desconto e conta mais um uso
        BigDecimal discount = BigDecimal.ZERO;
        if (request.couponCode() != null && !request.couponCode().isBlank()) {
            Coupon coupon = couponService.findValidForUse(request.couponCode());
            discount = couponService.calculateDiscount(coupon, subtotal);
            coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
            order.setCoupon(coupon);
        }

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTotal(subtotal.subtract(discount));

        // Snapshot: cópia do endereço e do total, que não muda mesmo se o cliente editar o endereço depois
        OrderSnapshot snapshot = new OrderSnapshot();
        snapshot.setOrder(order);
        snapshot.setAddressSnapshot(formatAddress(address));
        snapshot.setTotalSnapshot(order.getTotal());
        order.setSnapshot(snapshot);

        orderRepository.save(order);
        saveHistory(order, null, OrderStatus.AWAITING_PAYMENT, null, "Pedido criado");

        // Os itens comprados saem do carrinho
        cartItemRepository.deleteAll(cartItems);

        return order;
    }

    // Mudança de status feita por um administrador
    @Transactional
    public Orders changeStatus(Long id, OrderStatusRequest request) {
        Orders order = findById(id);
        Administrator admin = administratorService.findForAction(request.adminId());
        return changeStatus(order, request.status(), admin, request.notes());
    }

    // Usado também pelo pagamento (mudança automática, admin = null)
    @Transactional
    public Orders changeStatus(Orders order, OrderStatus newStatus, Administrator admin, String notes) {
        OrderStatus current = order.getStatus();

        if (!ALLOWED_TRANSITIONS.get(current).contains(newStatus)) {
            throw new BusinessException("order.invalid-transition",
                    "Pedido não pode ir de " + current + " para " + newStatus);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            // Pedido cancelado devolve o uso do cupom
            if (order.getCoupon() != null) {
                Coupon coupon = order.getCoupon();
                coupon.setCurrentUsage(coupon.getCurrentUsage() - 1);
            }

            // Pagamento já aprovado é estornado; PIX pendente é recusado
            updatePayments(order, PaymentStatus.APPROVED, PaymentStatus.REFUNDED, admin);
            updatePayments(order, PaymentStatus.PENDING, PaymentStatus.DECLINED, admin);
        }

        saveHistory(order, current, newStatus, admin, notes);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public Page<OrderStatusHistory> findStatusHistory(Long id, Pageable pageable) {
        findById(id);
        return historyRepository.findByOrderIdOrderByUpdateTimeDesc(id, pageable);
    }

    // Itens do carrinho que vão virar pedido: todos, ou só os ids informados
    private List<CartItem> findItemsToBuy(Long clientId, List<Long> cartItemIds) {
        Cart cart = cartRepository.findByClientId(clientId)
                .orElseThrow(() -> new BusinessException("order.cart-empty", "Cliente não tem carrinho"));

        List<CartItem> items = cartItemRepository.findByCartIdOrderByCreatedAt(cart.getId());

        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            items = items.stream().filter(item -> cartItemIds.contains(item.getId())).toList();

            if (items.size() != cartItemIds.size()) {
                throw new BusinessException("order.cart-item-invalid",
                        "Um ou mais itens não estão no carrinho do cliente: " + cartItemIds);
            }
        }

        if (items.isEmpty()) {
            throw new BusinessException("order.cart-empty", "Carrinho vazio");
        }

        return items;
    }

    private void updatePayments(Orders order, PaymentStatus from, PaymentStatus to, Administrator admin) {
        for (Payment payment : paymentRepository.findByOrderIdAndStatus(order.getId(), from)) {
            PaymentStatusHistory history = new PaymentStatusHistory();
            history.setPayment(payment);
            history.setChangedBy(admin);
            history.setPreviousStatus(from);
            history.setNewStatus(to);
            paymentHistoryRepository.save(history);

            payment.setStatus(to);
            paymentRepository.save(payment);
        }
    }

    private void saveHistory(Orders order, OrderStatus previous, OrderStatus next, Administrator admin,
                             String notes) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setPreviousStatus(previous);
        history.setNewStatus(next);
        history.setChangedBy(admin);
        history.setUpdateNotes(notes);
        historyRepository.save(history);
    }

    // Ex: "Av. Brasil, 100 - Ap 12 - Centro, Cascavel/PR - CEP 85800-000"
    private String formatAddress(Address a) {
        String complement = a.getComplement() != null && !a.getComplement().isBlank()
                ? " - " + a.getComplement() : "";

        return a.getStreet() + ", " + a.getNumber() + complement + " - " + a.getNeighborhood() + ", "
                + a.getCity() + "/" + a.getState() + " - CEP " + a.getZipCode();
    }
}
