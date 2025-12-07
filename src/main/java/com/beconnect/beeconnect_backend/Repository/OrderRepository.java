package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Order;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(o) FROM Order o WHERE o.product.seller = :seller")
    long countBySeller(@Param("seller") Person seller);

    @Query("SELECT o FROM Order o WHERE o.product.seller = :seller")
    List<Order> findBySeller(@Param("seller") Person seller);

    @Query("SELECT o FROM Order o WHERE o.product.seller = :seller ORDER BY o.orderedAt DESC")
    List<Order> findRecentOrdersBySeller(@Param("seller") Person seller);

    List<Order> findByBuyer(Person buyer);

    @Query("SELECT o FROM Order o WHERE o.buyer = :buyer ORDER BY o.orderedAt DESC")
    List<Order> findRecentOrdersByBuyer(@Param("buyer") Person buyer);
}