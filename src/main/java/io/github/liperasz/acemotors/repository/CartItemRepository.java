package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartIdOrderByCreatedAt(Long cartId);

    // Item do carrinho de um cliente específico (impede mexer no carrinho de outro cliente)
    Optional<CartItem> findByIdAndCartClientId(Long id, Long clientId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    void deleteByCartId(Long cartId);

    void deleteByCartClientId(Long clientId);
}
