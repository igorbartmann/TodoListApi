package com.example.todolistapi.model;

import lombok.Getter;
import lombok.Setter;

public class TodoItem {
    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private boolean completed;

    public TodoItem(int id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }
}
