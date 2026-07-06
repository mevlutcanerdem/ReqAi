package com.reqai.backend.entity;

public enum OutboxStatus {
    PENDING, // wait for sending
    COMPLETED, // message sent to kafka
    FAILED // failed while sending
}
