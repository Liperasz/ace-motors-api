package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.cart.CartItemQuantityRequest;
import io.github.liperasz.acemotors.dto.cart.CartItemRequest;
import io.github.liperasz.acemotors.dto.cart.CartItemResponse;
import io.github.liperasz.acemotors.dto.cart.CartResponse;
import io.github.liperasz.acemotors.entity.Cart;
import io.github.liperasz.acemotors.entity.CartItem;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.service.CartService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/cart")
public class CartController {

    private final CartService cartService;

    private final ApplicationEventPublisher publisher;

    public CartController (CartService cartService, ApplicationEventPublisher publisher) {
        this.cartService = cartService;
        this.publisher = publisher;
    }

    // Carrinho com os itens e o total (calculado com o preço atual dos produtos)
    @GetMapping
    public CartResponse find(@PathVariable Long clientId) {
        Cart cart = cartService.getOrCreateCart(clientId);
        List<CartItemResponse> items = cartService.findItems(cart.getId()).stream()
                .map(CartItemResponse::from)
                .toList();
        return CartResponse.from(cart.getId(), items);
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(@PathVariable Long clientId,
                                                    @Valid @RequestBody CartItemRequest request,
                                                    HttpServletResponse response) {
        CartItem item = cartService.addItem(clientId, request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, item.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CartItemResponse.from(item));
    }

    @PutMapping("/items/{itemId}")
    public CartItemResponse updateQuantity(@PathVariable Long clientId, @PathVariable Long itemId,
                                           @Valid @RequestBody CartItemQuantityRequest request) {
        return CartItemResponse.from(cartService.updateQuantity(clientId, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long clientId, @PathVariable Long itemId) {
        cartService.removeItem(clientId, itemId);
    }

    // Esvazia o carrinho
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@PathVariable Long clientId) {
        cartService.clear(clientId);
    }
}
