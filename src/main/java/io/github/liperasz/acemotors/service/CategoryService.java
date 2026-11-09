package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.category.CategoryRequest;
import io.github.liperasz.acemotors.entity.Category;
import io.github.liperasz.acemotors.repository.CategoryRepository;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService (CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<Category> findAll(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    // Busca pelo id ou lança 404
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
    }

    @Transactional
    public Category create(CategoryRequest request) {
        Category category = new Category();
        copyToEntity(request, category);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, CategoryRequest request) {
        Category category = findById(id);
        copyToEntity(request, category);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        // deleteById não avisa quando o id não existe, por isso busca antes
        Category category = findById(id);
        categoryRepository.delete(category);
        // flush força o banco a validar as FKs aqui (ex: categoria ainda ligada a produtos)
        categoryRepository.flush();
    }

    private void copyToEntity(CategoryRequest request, Category category) {
        category.setName(request.name());
        category.setDescription(request.description());
    }
}
