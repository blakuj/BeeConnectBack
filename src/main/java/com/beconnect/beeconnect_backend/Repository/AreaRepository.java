package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByOwner(Person owner);
    List<Area> findByStatus(String status);
    long countByStatus(String status);

    @Query("SELECT a FROM Area a WHERE a.status = :status ORDER BY a.dateAdded DESC")
    List<Area> findByStatusOrderByDateAddedDesc(@Param("status") String status);

    @Query("SELECT a FROM Area a ORDER BY a.dateAdded DESC")
    List<Area> findAllOrderByDateAddedDesc();
}