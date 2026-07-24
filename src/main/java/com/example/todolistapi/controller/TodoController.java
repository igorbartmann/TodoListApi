package com.example.todolistapi.controller;

import com.example.todolistapi.model.TodoItem;
import com.example.todolistapi.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoItem> getAll() {
        return todoService.getAll();
    }

    @GetMapping("/{id}")
    public TodoItem getById(@PathVariable int id) {
        return this.todoService.getById(id);
    }

    @PostMapping
    public TodoItem create(@RequestParam String description) {
        return this.todoService.create(description);
    }

    @PutMapping("/{id}/complete")
    public TodoItem completeTodoItem(@PathVariable int id) {
        return this.todoService.complete(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        boolean todoItemWasDeleted = this.todoService.delete(id);

        if (todoItemWasDeleted) {
            return "Item deleted";
        }

        return "Item now found.";
    }
}
