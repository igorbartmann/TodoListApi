package com.example.todolistapi.model;

import lombok.Getter;
import lombok.Setter;

public class TodoItemInputModel {
    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private boolean completed;
}
