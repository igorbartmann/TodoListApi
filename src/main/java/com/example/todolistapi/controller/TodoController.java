package com.example.todolistapi.controller;

import com.example.todolistapi.entity.TodoItem;
import com.example.todolistapi.model.TodoItemInputModel;
import com.example.todolistapi.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TodoItem>> getAll(@RequestParam(name = "onlyIncomplete", required = false, defaultValue = "false") boolean onlyIncomplete) {
        List<TodoItem> todoItems = onlyIncomplete
                ? this.todoService.getAllIncomplete()
                : this.todoService.getAll();

        return ResponseEntity.ok(todoItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoItem> getById(@PathVariable int id) {
        TodoItem todoItem = this.todoService.getById(id);

        return todoItem != null
                ? ResponseEntity.ok(todoItem)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TodoItem> create(@RequestBody TodoItemInputModel model) {
        TodoItem todoItem = this.todoService.create(model);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(todoItem.getId())
            .toUri();

        return ResponseEntity.created(location).body(todoItem);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TodoItem> completeTodoItem(@PathVariable int id) {
        TodoItem todoItem = this.todoService.complete(id);

        return todoItem != null
                ? ResponseEntity.ok(todoItem)
                : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        boolean todoItemWasDeleted = this.todoService.delete(id);

        if (todoItemWasDeleted) {
            return ResponseEntity.ok("The item was successfully deleted.");
        }

        return ResponseEntity.notFound().build();
    }
}
