package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Order;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import com.beconnect.beeconnect_backend.Model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByOrderProductOrderByCreatedAtDesc(Product product);

    Optional<ProductReview> findByOrder(Order order);

    boolean existsByOrder(Order order);

    List<ProductReview> findByOrderBuyerOrderByCreatedAtDesc(Person buyer);

    long countByOrderProduct(Product product);

    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.order.product = :product")
    Double getAverageRatingByProduct(@Param("product") Product product);
}