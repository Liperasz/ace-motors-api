package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.review.ReviewRequest;
import io.github.liperasz.acemotors.entity.OrderItem;
import io.github.liperasz.acemotors.entity.Review;
import io.github.liperasz.acemotors.enums.OrderStatus;
import io.github.liperasz.acemotors.repository.OrderItemRepository;
import io.github.liperasz.acemotors.repository.ReviewRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Avaliação (RF09): feita sobre um item de pedido, garantindo que o cliente realmente comprou o produto
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final OrderItemRepository orderItemRepository;

    private final ClientService clientService;

    private final ProductService productService;

    public ReviewService (ReviewRepository reviewRepository, OrderItemRepository orderItemRepository,
                          ClientService clientService, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
        this.clientService = clientService;
        this.productService = productService;
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação", id));
    }

    public Page<Review> findByProduct(Long productId, Pageable pageable) {
        productService.findById(productId);
        return reviewRepository.findByOrderItemProductId(productId, pageable);
    }

    public Double averageRating(Long productId) {
        return reviewRepository.averageRating(productId);
    }

    // Só avalia: item de pedido ENTREGUE, de cliente ativo, uma única vez
    @Transactional
    public Review create(Long orderItemId, ReviewRequest request) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do pedido", orderItemId));

        if (item.getOrder().getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("review.order-not-delivered",
                    "Pedido " + item.getOrder().getId() + " está com status " + item.getOrder().getStatus());
        }

        clientService.findActive(item.getOrder().getClient().getId());

        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            throw new BusinessException("review.already-exists",
                    "Item " + orderItemId + " já foi avaliado", HttpStatus.CONFLICT);
        }

        Review review = new Review();
        review.setOrderItem(item);
        review.setRating(request.rating());
        review.setComment(request.comment());
        return reviewRepository.save(review);
    }

    @Transactional
    public Review update(Long id, ReviewRequest request) {
        Review review = findById(id);
        clientService.findActive(review.getOrderItem().getOrder().getClient().getId());

        review.setRating(request.rating());
        review.setComment(request.comment());
        return reviewRepository.save(review);
    }

    @Transactional
    public void delete(Long id) {
        reviewRepository.delete(findById(id));
    }
}
