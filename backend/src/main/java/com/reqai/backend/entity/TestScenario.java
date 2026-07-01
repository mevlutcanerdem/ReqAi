package com.reqai.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_scenarios")
@Getter
@Setter
public class TestScenario extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",nullable = false)
    private Task task;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String description;

    @Column(name = "expected_result",columnDefinition = "TEXT",nullable = false)
    private String expectedResult;

}
