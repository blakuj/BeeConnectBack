package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByEmail(String email);
    Optional<Person> findByLogin(String login);
    Optional<Person> findByEmail(String email);
}
