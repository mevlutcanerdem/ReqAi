package com.reqai.backend.repository;

import com.reqai.backend.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequirementRepository extends JpaRepository<Requirement,UUID> {
    List<Requirement> findByDocumentId(UUID documentId);
}
