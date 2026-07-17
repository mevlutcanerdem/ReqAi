package com.reqai.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document extends BaseEntity{

    @Column(name = "file_name",nullable = false)
    private String fileName;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;

    // one document has many requirements one-to-many relation
    @OneToMany(mappedBy = "document",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Requirement> requirements = new ArrayList<>();

    // logic :many document has one user many-to-one relation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

}
