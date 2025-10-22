package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.BeeGarden;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeeGardenRepository extends JpaRepository<BeeGarden, Long> {
    // Znajdź wszystkie pasieki należące do danej osoby
    List<BeeGarden> findByPerson(Person person);
}