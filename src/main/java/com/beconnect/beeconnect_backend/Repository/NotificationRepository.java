package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Notification;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.user = :user ORDER BY n.isRead ASC, n.createdAt DESC")
    List<Notification> findByUserOrderByIsReadAscCreatedAtDesc(@Param("user") Person user);

    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUser(@Param("user") Person user);

    // Policz nieprzeczytane powiadomienia
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    long countUnreadByUser(@Param("user") Person user);

    // Oznacz wszystkie powiadomienia jako przeczytane
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") Person user);

    // Usuń stare przeczytane powiadomienia (starsze niż X dni)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user = :user AND n.isRead = true AND n.createdAt < :date")
    void deleteOldReadNotifications(@Param("user") Person user, @Param("date") LocalDateTime date);
}