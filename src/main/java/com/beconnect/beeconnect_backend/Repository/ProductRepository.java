package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Znajdź produkty według kategorii
    List<Product> findByCategory(ProductCategory category);

    // Znajdź produkty według sprzedawcy
    List<Product> findBySeller(Person seller);

    // Znajdź dostępne produkty
    List<Product> findByAvailableTrue();

    // Znajdź produkty w określonym przedziale cenowym
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice AND p.available = true")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Wyszukiwanie po nazwie (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.available = true")
    List<Product> searchByName(@Param("searchTerm") String searchTerm);

    // Znajdź produkty według kategorii i dostępności
    List<Product> findByCategoryAndAvailableTrue(ProductCategory category);

    // Znajdź produkty z oceną powyżej określonej wartości
    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating AND p.available = true")
    List<Product> findByMinRating(@Param("minRating") Double minRating);

    // Znajdź najnowsze produkty
    @Query("SELECT p FROM Product p WHERE p.available = true ORDER BY p.createdAt DESC")
    List<Product> findRecentProducts();

    // Znajdź najpopularniejsze produkty (według liczby recenzji)
    @Query("SELECT p FROM Product p WHERE p.available = true ORDER BY p.reviewCount DESC")
    List<Product> findPopularProducts();

    // Zlicz produkty według kategorii
    long countByCategory(ProductCategory category);

    // Zlicz produkty sprzedawcy
    long countBySeller(Person seller);
}