package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Avaliações de um produto (navegando review -> orderItem -> product)
    Page<Review> findByOrderItemProductId(Long productId, Pageable pageable);

    boolean existsByOrderItemId(Long orderItemId);

    // Média das notas de um produto (null se ainda não tem avaliação)
    @Query("select avg(r.rating) from Review r where r.orderItem.product.id = :productId")
    Double averageRating(Long productId);
}
