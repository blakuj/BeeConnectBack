package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByCode(String code);

    boolean existsByCode(String code);
}