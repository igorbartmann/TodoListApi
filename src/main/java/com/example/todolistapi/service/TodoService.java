package com.example.todolistapi.service;

import com.example.todolistapi.entity.TodoItem;
import com.example.todolistapi.model.TodoItemInputModel;
import com.example.todolistapi.repository.TodoItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    public List<TodoItem> getAll() {
        return repository.findAll();
    }

    public List<TodoItem> getAllIncomplete() {
        return repository.getAllIncomplete();
    }

    public TodoItem getById(int id) {
        return repository
            .findById(id)
            .orElse(null);
    }

    public TodoItem create(TodoItemInputModel model) {
        TodoItem newTodoItem = new TodoItem(model.getDescription(), model.isCompleted());

        repository.save(newTodoItem);
        return newTodoItem;
    }

    public TodoItem complete(int id) {
        TodoItem todoItem = repository
            .findById(id)
            .orElse(null);

        if (todoItem == null || todoItem.isCompleted()) {
            return null;
        }

        todoItem.setCompleted(true);
        repository.save(todoItem);

        return todoItem;
    }

    public boolean delete(int id) {
        if (!repository.existsById(id)) {
            return false;
        }

        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
