package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.OrderStatus;
import com.beconnect.beeconnect_backend.Model.Order;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Znajdź wszystkie zamówienia kupującego
    List<Order> findByBuyer(Person buyer);

    // Znajdź wszystkie zamówienia sprzedawcy
    List<Order> findBySeller(Person seller);

    // Znajdź zamówienia według statusu dla kupującego
    List<Order> findByBuyerAndStatus(Person buyer, OrderStatus status);

    // Znajdź zamówienia według statusu dla sprzedawcy
    List<Order> findBySellerAndStatus(Person seller, OrderStatus status);

    // Znajdź zamówienia dla konkretnego produktu
    List<Order> findByProduct(Product product);

    // Znajdź najnowsze zamówienia kupującego
    @Query("SELECT o FROM Order o WHERE o.buyer = :buyer ORDER BY o.orderedAt DESC")
    List<Order> findRecentOrdersByBuyer(Person buyer);

    // Znajdź najnowsze zamówienia sprzedawcy
    @Query("SELECT o FROM Order o WHERE o.seller = :seller ORDER BY o.orderedAt DESC")
    List<Order> findRecentOrdersBySeller(Person seller);

    // Policz zamówienia według statusu
    long countByStatus(OrderStatus status);

    // Policz wszystkie zamówienia użytkownika jako kupującego
    long countByBuyer(Person buyer);

    // Policz wszystkie zamówienia użytkownika jako sprzedawcy
    long countBySeller(Person seller);
}