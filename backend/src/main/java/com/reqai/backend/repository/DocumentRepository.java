package com.reqai.backend.repository;

import com.reqai.backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository  extends JpaRepository<Document, UUID> {


    List<Document> findByUser_Username(String username);
}
