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

    /**
     * Utwórz opinię o produkcie
     */
    @Transactional
    public ProductReviewDTO createReview(CreateProductReviewDTO dto) {
        Person currentUser = personService.getProfile();

        // Walidacja
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Pobierz zamówienie
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Sprawdź czy zamówienie należy do użytkownika
        if (!order.getBuyer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only review your own orders");
        }

        // Sprawdź czy już nie wystawiono opinii
        if (reviewRepository.existsByOrder(order)) {
            throw new RuntimeException("You have already reviewed this order");
        }

        // Utwórz opinię
        ProductReview review = ProductReview.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .reviewer(currentUser)
                .product(order.getProduct())
                .order(order)
                .build();

        review = reviewRepository.save(review);

        // Aktualizuj rating produktu
        updateProductRating(order.getProduct());

        return mapToDTO(review);
    }

    /**
     * Pobierz opinie dla produktu
     */
    public List<ProductReviewDTO> getProductReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ProductReview> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz opinie użytkownika
     */
    public List<ProductReviewDTO> getMyReviews() {
        Person currentUser = personService.getProfile();
        List<ProductReview> reviews = reviewRepository.findByReviewerOrderByCreatedAtDesc(currentUser);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sprawdź czy zamówienie może być ocenione
     */
    public boolean canReviewOrder(Long orderId) {
        Person currentUser = personService.getProfile();

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;

        // Sprawdź czy zamówienie należy do użytkownika
        if (!order.getBuyer().getId().equals(currentUser.getId())) {
            return false;
        }

        // Sprawdź czy już nie wystawiono opinii
        return !reviewRepository.existsByOrder(order);
    }

    /**
     * Aktualizuj średni rating i liczbę opinii dla produktu
     */
    @Transactional
    public void updateProductRating(Product product) {
        Double averageRating = reviewRepository.getAverageRatingByProduct(product);
        long reviewCount = reviewRepository.countByProduct(product);

        product.setRating(averageRating != null ? averageRating : 0.0);
        product.setReviewCount((int) reviewCount);
        productRepository.save(product);
    }

    /**
     * Mapowanie ProductReview → ProductReviewDTO
     */
    private ProductReviewDTO mapToDTO(ProductReview review) {
        return ProductReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reviewerId(review.getReviewer().getId())
                .reviewerFirstname(review.getReviewer().getFirstname())
                .reviewerLastname(review.getReviewer().getLastname())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .orderId(review.getOrder().getId())
                .build();
    }
}