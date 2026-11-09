package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.SavedPaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedPaymentMethodRepository extends JpaRepository<SavedPaymentMethod, Long> {

    Page<SavedPaymentMethod> findByClientId(Long clientId, Pageable pageable);

    Optional<SavedPaymentMethod> findByIdAndClientId(Long id, Long clientId);

    void deleteByClientId(Long clientId);
}
