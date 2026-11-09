package io.github.liperasz.acemotors.dto.review;

import org.springframework.data.web.PagedModel;

// Avaliações de um produto + média das notas
public record ProductReviewsResponse(Long productId, Double averageRating, PagedModel<ReviewResponse> reviews) {
}
