package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
}
