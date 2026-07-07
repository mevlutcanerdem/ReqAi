package com.reqai.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
public class OutboxEvent extends BaseEntity {



    @Column(name = "document_id",nullable = false)
    private UUID documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING; // as a default it starts with PENDING

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }
}

