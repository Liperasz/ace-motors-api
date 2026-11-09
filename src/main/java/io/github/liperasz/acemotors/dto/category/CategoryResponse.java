package io.github.liperasz.acemotors.dto.category;

import io.github.liperasz.acemotors.entity.Category;

// Dados devolvidos pela API (nunca a entidade direto)
public record CategoryResponse(Long id, String name, String description) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
