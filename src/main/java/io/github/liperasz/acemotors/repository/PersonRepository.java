package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

// Usado para regras que valem para cliente e administrador (ex: e-mail único)
public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByEmail(String email);

    // Mesmo e-mail, mas de outra pessoa (usado na atualização)
    boolean existsByEmailAndIdNot(String email, Long id);
}
