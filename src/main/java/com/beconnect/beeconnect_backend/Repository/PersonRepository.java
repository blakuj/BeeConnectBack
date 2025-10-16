package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByLogin(String login);
    Optional<Person> findByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);

    // Nowe metody dla panelu admina
    List<Person> findByRole(Role role);
    long countByRole(Role role);
    List<Person> findByActive(boolean active);

    @Query("SELECT p FROM Person p WHERE " +
            "LOWER(p.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Person> searchUsers(@Param("search") String search);

    @Query("SELECT p FROM Person p WHERE p.role = :role AND " +
            "(LOWER(p.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Person> searchUsersByRole(@Param("search") String search, @Param("role") Role role);
}