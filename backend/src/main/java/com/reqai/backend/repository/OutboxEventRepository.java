package com.reqai.backend.repository;

import com.reqai.backend.entity.OutboxEvent;

import com.reqai.backend.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    // race condition (bottleneck: instance A and instance B read the same rows and try to send message to kafka
    // and using the limit operation we won't get out of memory error
    @Query(value = """
            SELECT * FROM outbox_events
            WHERE status = 'PENDING'
            ORDER BY created_at ASC
            FOR UPDATE SKIP LOCKED   
            LIMIT 10
            """,nativeQuery = true)
    List<OutboxEvent> findPendingEventsForProcessing();
    Optional<OutboxEvent> findByDocumentId(UUID documentId);
}
