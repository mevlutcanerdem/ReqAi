CREATE TABLE outbox_events(
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);