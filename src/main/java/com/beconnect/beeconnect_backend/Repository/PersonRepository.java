package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    long countByRole(Role role);
}