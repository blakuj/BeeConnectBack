package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Conversation;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByBuyerAndProduct(Person buyer, Product product);

    @Query("SELECT c FROM Conversation c WHERE c.buyer = :user OR c.product.seller = :user")
    List<Conversation> findAllByParticipant(@Param("user") Person user);
}