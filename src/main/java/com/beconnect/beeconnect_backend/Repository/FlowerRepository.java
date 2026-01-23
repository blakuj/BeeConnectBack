package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Flower;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Long> {
    Optional<Flower> findByName(String name);
}