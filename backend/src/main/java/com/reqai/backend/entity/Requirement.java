package com.reqai.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requirements")
@Getter
@Setter
public class Requirement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id",nullable = false)
    private Document document;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String description;

    private String priority;

    @OneToMany(mappedBy = "requirement",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();


}
