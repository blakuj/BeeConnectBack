package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Conversation;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Znajdź wszystkie konwersacje użytkownika
    @Query("SELECT c FROM Conversation c WHERE c.participant1 = :user OR c.participant2 = :user ORDER BY c.lastMessageAt DESC")
    List<Conversation> findByUser(@Param("user") Person user);

    // Znajdź konwersację między dwoma użytkownikami
    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.participant1 = :user1 AND c.participant2 = :user2) OR " +
            "(c.participant1 = :user2 AND c.participant2 = :user1)")
    Optional<Conversation> findByParticipants(@Param("user1") Person user1, @Param("user2") Person user2);

    // Sprawdź czy istnieje konwersacja między dwoma użytkownikami
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Conversation c WHERE " +
            "(c.participant1 = :user1 AND c.participant2 = :user2) OR " +
            "(c.participant1 = :user2 AND c.participant2 = :user1)")
    boolean existsByParticipants(@Param("user1") Person user1, @Param("user2") Person user2);
}