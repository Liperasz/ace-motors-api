package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.repository.client.ClientRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long>, ClientRepositoryQuery {
}
