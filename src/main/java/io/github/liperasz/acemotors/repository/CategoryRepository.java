package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Busca por parte do nome, sem diferenciar maiúsculas/minúsculas
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
