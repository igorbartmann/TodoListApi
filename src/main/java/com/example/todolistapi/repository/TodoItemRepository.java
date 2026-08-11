package com.example.todolistapi.repository;

import com.example.todolistapi.entity.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Integer> {
    @Query(value = "SELECT * FROM TODOITEM WHERE COMPLETED = 0", nativeQuery = true)
    List<TodoItem> getAllIncomplete();
}
