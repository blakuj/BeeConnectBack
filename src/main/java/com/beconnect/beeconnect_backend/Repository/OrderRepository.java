package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.OrderStatus;
import com.beconnect.beeconnect_backend.Model.Order;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyer(Person buyer);

    List<Order> findBySeller(Person seller);

    List<Order> findByBuyerAndStatus(Person buyer, OrderStatus status);

    List<Order> findBySellerAndStatus(Person seller, OrderStatus status);

    List<Order> findByProduct(Product product);

    @Query("SELECT o FROM Order o WHERE o.buyer = :buyer ORDER BY o.orderedAt DESC")
    List<Order> findRecentOrdersByBuyer(Person buyer);

    @Query("SELECT o FROM Order o WHERE o.seller = :seller ORDER BY o.orderedAt DESC")
    List<Order> findRecentOrdersBySeller(Person seller);

    long countByStatus(OrderStatus status);

    long countByBuyer(Person buyer);

    long countBySeller(Person seller);
}