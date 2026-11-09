package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.cart.CartItemQuantityRequest;
import io.github.liperasz.acemotors.dto.cart.CartItemRequest;
import io.github.liperasz.acemotors.entity.Cart;
import io.github.liperasz.acemotors.entity.CartItem;
import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.repository.CartItemRepository;
import io.github.liperasz.acemotors.repository.CartRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    // Limite de unidades do mesmo produto no carrinho
    private static final int MAX_QUANTITY = 99;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ClientService clientService;

    private final ProductService productService;

    public CartService (CartRepository cartRepository, CartItemRepository cartItemRepository,
                        ClientService clientService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.clientService = clientService;
        this.productService = productService;
    }

    // O carrinho é criado na primeira vez que o cliente acessa
    @Transactional
    public Cart getOrCreateCart(Long clientId) {
        Client client = clientService.findById(clientId);

        return cartRepository.findByClientId(clientId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setClient(client);
            return cartRepository.save(cart);
        });
    }

    public List<CartItem> findItems(Long cartId) {
        return cartItemRepository.findByCartIdOrderByCreatedAt(cartId);
    }

    // Se o produto já está no carrinho, soma a quantidade na mesma linha
    @Transactional
    public CartItem addItem(Long clientId, CartItemRequest request) {
        clientService.findActive(clientId);
        Product product = productService.findAvailable(request.productId());
        Cart cart = getOrCreateCart(clientId);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int newQuantity = item.getQuantity() + request.quantity();
        if (newQuantity > MAX_QUANTITY) {
            throw new BusinessException("cart.quantity-limit",
                    "Quantidade total " + newQuantity + " passa do limite de " + MAX_QUANTITY);
        }

        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    @Transactional
    public CartItem updateQuantity(Long clientId, Long itemId, CartItemQuantityRequest request) {
        clientService.findActive(clientId);
        CartItem item = findItem(clientId, itemId);
        productService.findAvailable(item.getProduct().getId());

        item.setQuantity(request.quantity());
        return cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(Long clientId, Long itemId) {
        cartItemRepository.delete(findItem(clientId, itemId));
    }

    // Esvazia o carrinho (o carrinho em si continua existindo)
    @Transactional
    public void clear(Long clientId) {
        Cart cart = getOrCreateCart(clientId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private CartItem findItem(Long clientId, Long itemId) {
        return cartItemRepository.findByIdAndCartClientId(itemId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho", itemId));
    }
}
