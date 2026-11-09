package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Payment;
import io.github.liperasz.acemotors.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId, Pageable pageable);

    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
