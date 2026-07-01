package com.reqai.backend.repository;

import com.reqai.backend.entity.TestScenario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestScenarioRepository extends JpaRepository<TestScenario, UUID> {
}
