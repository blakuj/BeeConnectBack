package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Order;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import com.beconnect.beeconnect_backend.Model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    // Znajdź opinie dla produktu
    List<ProductReview> findByProductOrderByCreatedAtDesc(Product product);

    // Znajdź opinię dla konkretnego zamówienia
    Optional<ProductReview> findByOrder(Order order);

    // Sprawdź czy zamówienie ma już opinię
    boolean existsByOrder(Order order);

    // Znajdź opinie wystawione przez użytkownika
    List<ProductReview> findByReviewerOrderByCreatedAtDesc(Person reviewer);

    // Policz opinie dla produktu
    long countByProduct(Product product);

    // Średnia ocena dla produktu
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product = :product")
    Double getAverageRatingByProduct(Product product);
}