package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Só os endereços ativos (os "excluídos" que já foram usados em pedidos ficam com active = false)
    Page<Address> findByPersonIdAndActiveTrue(Long personId, Pageable pageable);

    Optional<Address> findByIdAndPersonIdAndActiveTrue(Long id, Long personId);

    void deleteByPersonId(Long personId);
}
