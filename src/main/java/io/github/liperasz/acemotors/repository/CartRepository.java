package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Cada cliente tem no máximo um carrinho
    Optional<Cart> findByClientId(Long clientId);

    void deleteByClientId(Long clientId);
}
