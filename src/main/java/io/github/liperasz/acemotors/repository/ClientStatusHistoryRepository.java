package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.ClientStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientStatusHistoryRepository extends JpaRepository<ClientStatusHistory, Long> {

    Page<ClientStatusHistory> findByClientIdOrderByUpdateTimeDesc(Long clientId, Pageable pageable);
}
