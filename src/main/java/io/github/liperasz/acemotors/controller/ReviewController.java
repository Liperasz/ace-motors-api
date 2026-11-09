package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.review.ProductReviewsResponse;
import io.github.liperasz.acemotors.dto.review.ReviewRequest;
import io.github.liperasz.acemotors.dto.review.ReviewResponse;
import io.github.liperasz.acemotors.entity.Review;
import io.github.liperasz.acemotors.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController (ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Avaliar um item de pedido entregue -> 201 + Location: /reviews/{id}
    @PostMapping("/order-items/{orderItemId}/review")
    public ResponseEntity<ReviewResponse> create(@PathVariable Long orderItemId,
                                                 @Valid @RequestBody ReviewRequest request) {
        Review review = reviewService.create(orderItemId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/reviews/{id}")
                .buildAndExpand(review.getId())
                .toUri();

        return ResponseEntity.created(location).body(ReviewResponse.from(review));
    }

    // Avaliações de um produto (paginado) + média das notas
    @GetMapping("/products/{productId}/reviews")
    public ProductReviewsResponse findByProduct(@PathVariable Long productId, Pageable pageable) {
        PagedModel<ReviewResponse> reviews = new PagedModel<>(
                reviewService.findByProduct(productId, pageable).map(ReviewResponse::from));

        return new ProductReviewsResponse(productId, reviewService.averageRating(productId), reviews);
    }

    @GetMapping("/reviews/{id}")
    public ReviewResponse findById(@PathVariable Long id) {
        return ReviewResponse.from(reviewService.findById(id));
    }

    @PutMapping("/reviews/{id}")
    public ReviewResponse update(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ReviewResponse.from(reviewService.update(id, request));
    }

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}
