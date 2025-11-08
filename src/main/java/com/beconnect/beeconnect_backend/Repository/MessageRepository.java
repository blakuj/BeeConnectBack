package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Conversation;
import com.beconnect.beeconnect_backend.Model.Message;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.sender != :user AND m.isRead = false")
    List<Message> findUnreadMessages(@Param("conversation") Conversation conversation, @Param("user") Person user);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.sender != :user AND m.isRead = false")
    long countUnreadMessages(@Param("conversation") Conversation conversation, @Param("user") Person user);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation = :conversation AND m.sender != :user AND m.isRead = false")
    void markAllAsRead(@Param("conversation") Conversation conversation, @Param("user") Person user);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.sentAt DESC LIMIT 1")
    Message findLastMessage(@Param("conversation") Conversation conversation);
}