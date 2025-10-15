package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
