package com.example.todolistapi.service;

import com.example.todolistapi.model.TodoItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TodoService {
    private int nextId = 1;
    private final List<TodoItem> todoList = new ArrayList<TodoItem>();

    public TodoService() {
        todoList.add(new TodoItem(nextId++, "First Item", true));
        todoList.add(new TodoItem(nextId++, "Second Item", false));
    }

    public List<TodoItem> getAll() {
        return todoList;
    }

    public TodoItem getById(int id) {
        return todoList
            .stream()
            .filter(ti -> ti.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public TodoItem create(String description) {
        TodoItem newTodoItem = new TodoItem(nextId++, description, false);
        todoList.add(newTodoItem);
        return newTodoItem;
    }

    public TodoItem complete(int id) {
        var todoItem = todoList
            .stream()
            .filter(ti -> ti.getId() == id)
            .findFirst()
            .orElse(null);

        if (todoItem == null) {
            return null;
        }

        todoItem.setCompleted(true);
        return todoItem;
    }

    public boolean delete(int id) {
        return this.todoList.removeIf(ti -> ti.getId() == id);
    }
}
