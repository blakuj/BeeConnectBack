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

    // Znajdź opinie dla produktu (bez zmian, korzystamy z product_id)
    List<ProductReview> findByProductOrderByCreatedAtDesc(Product product);

    Optional<ProductReview> findByOrder(Order order);

    boolean existsByOrder(Order order);

    List<ProductReview> findByOrderBuyerOrderByCreatedAtDesc(Person buyer);

    long countByProduct(Product product);

    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product = :product")
    Double getAverageRatingByProduct(Product product);
}