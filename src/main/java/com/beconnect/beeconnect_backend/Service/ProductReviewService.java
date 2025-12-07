package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.CreateProductReviewDTO;
import com.beconnect.beeconnect_backend.DTO.ProductReviewDTO;
import com.beconnect.beeconnect_backend.Model.Order;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import com.beconnect.beeconnect_backend.Model.ProductReview;
import com.beconnect.beeconnect_backend.Repository.OrderRepository;
import com.beconnect.beeconnect_backend.Repository.ProductRepository;
import com.beconnect.beeconnect_backend.Repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PersonService personService;

    @Transactional
    public ProductReviewDTO createReview(CreateProductReviewDTO dto) {
        Person currentUser = personService.getProfile();

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only review your own orders");
        }

        if (reviewRepository.existsByOrder(order)) {
            throw new RuntimeException("You have already reviewed this order");
        }

        // ZMIANA: Nie ustawiamy product
        ProductReview review = ProductReview.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .order(order)
                .build();

        review = reviewRepository.save(review);

        updateProductRating(order.getProduct());

        return mapToDTO(review);
    }

    public List<ProductReviewDTO> getProductReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ZMIANA: nowa metoda repozytorium
        List<ProductReview> reviews = reviewRepository.findByOrderProductOrderByCreatedAtDesc(product);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductReviewDTO> getMyReviews() {
        Person currentUser = personService.getProfile();
        // Ta metoda repozytorium była już poprawna (przechodziła przez OrderBuyer)
        List<ProductReview> reviews = reviewRepository.findByOrderBuyerOrderByCreatedAtDesc(currentUser);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public boolean canReviewOrder(Long orderId) {
        // ... (bez zmian) ...
        Person currentUser = personService.getProfile();
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (!order.getBuyer().getId().equals(currentUser.getId())) return false;
        return !reviewRepository.existsByOrder(order);
    }

    @Transactional
    public void updateProductRating(Product product) {
        Double averageRating = reviewRepository.getAverageRatingByProduct(product);
        // ZMIANA: nowa metoda repozytorium
        long reviewCount = reviewRepository.countByOrderProduct(product);

        product.setRating(averageRating != null ? averageRating : 0.0);
        product.setReviewCount((int) reviewCount);
        productRepository.save(product);
    }

    private ProductReviewDTO mapToDTO(ProductReview review) {
        Person reviewer = review.getOrder().getBuyer();
        Product product = review.getOrder().getProduct();

        return ProductReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reviewerId(reviewer.getId())
                .reviewerFirstname(reviewer.getFirstname())
                .reviewerLastname(reviewer.getLastname())
                .productId(product.getId())
                .productName(product.getName())
                .orderId(review.getOrder().getId())
                .build();
    }
}