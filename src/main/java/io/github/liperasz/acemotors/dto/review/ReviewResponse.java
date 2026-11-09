package io.github.liperasz.acemotors.dto.review;

import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.entity.OrderItem;
import io.github.liperasz.acemotors.entity.Review;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long orderItemId,
        Long productId,
        String productName,
        Long clientId,
        String clientName,
        Integer rating,
        String comment,
        Instant createdAt
) {

    public static ReviewResponse from(Review review) {
        OrderItem item = review.getOrderItem();
        Client client = item.getOrder().getClient();

        return new ReviewResponse(review.getId(), item.getId(), item.getProduct().getId(),
                item.getProduct().getName(), client.getId(), client.getName(), review.getRating(),
                review.getComment(), review.getCreatedAt());
    }
}
