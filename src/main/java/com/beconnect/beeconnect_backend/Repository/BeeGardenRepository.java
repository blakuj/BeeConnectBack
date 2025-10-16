package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.BeeGarden;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeeGardenRepository extends JpaRepository<BeeGarden, Long> {
    Optional<BeeGarden> findByPerson(Person person);
}