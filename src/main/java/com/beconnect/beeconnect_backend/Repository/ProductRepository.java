package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(ProductCategory category);

    List<Product> findBySeller(Person seller);

    List<Product> findByAvailableTrue();

    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice AND p.available = true")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.available = true")
    List<Product> searchByName(@Param("searchTerm") String searchTerm);

    List<Product> findByCategoryAndAvailableTrue(ProductCategory category);

    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating AND p.available = true")
    List<Product> findByMinRating(@Param("minRating") Double minRating);

    @Query("SELECT p FROM Product p WHERE p.available = true ORDER BY p.createdAt DESC")
    List<Product> findRecentProducts();

    @Query("SELECT p FROM Product p WHERE p.available = true ORDER BY p.reviewCount DESC")
    List<Product> findPopularProducts();

    long countByCategory(ProductCategory category);

    long countBySeller(Person seller);
}