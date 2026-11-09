package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.PaymentStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, Long> {

    Page<PaymentStatusHistory> findByPaymentIdOrderByUpdateTimeDesc(Long paymentId, Pageable pageable);
}
