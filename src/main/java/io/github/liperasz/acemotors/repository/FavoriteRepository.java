package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Page<Favorite> findByClientId(Long clientId, Pageable pageable);

    Optional<Favorite> findByIdAndClientId(Long id, Long clientId);

    boolean existsByClientIdAndProductId(Long clientId, Long productId);

    void deleteByClientId(Long clientId);
}
