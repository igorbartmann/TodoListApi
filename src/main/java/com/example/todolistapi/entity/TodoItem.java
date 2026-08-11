package com.example.todolistapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TODOITEM")
public class TodoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter
    @Setter
    private int id;

    @Column(name = "DESCRIPTION", nullable = false)
    @Getter
    @Setter
    private String description;

    @Column(name = "COMPLETED", nullable = false)
    @Getter
    @Setter
    private boolean completed;

    // Default constructor required by Hibernate/JPA.
    protected TodoItem() {

    }

    public TodoItem(String description, boolean completed) {
        this.description = description;
        this.completed = completed;
    }
}
