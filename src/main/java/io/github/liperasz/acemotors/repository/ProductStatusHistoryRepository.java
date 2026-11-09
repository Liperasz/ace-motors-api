package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.ProductStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatusHistoryRepository extends JpaRepository<ProductStatusHistory, Long> {

    // Histórico de um produto, do mais recente para o mais antigo
    Page<ProductStatusHistory> findByProductIdOrderByUpdateTimeDesc(Long productId, Pageable pageable);
}
