package com.reqai.backend.entity;

public enum OutboxStatus {
    PENDING, // wait for sending
    PROCESSED, // message sent to kafka
    FAILED // failed while sending
}
