# JAVA

## CREATING AN API PROJECT
[1] - Open IntelliJ
[2] - Click New Project
[3] - Enter
> Project Name
> Select Maven
[4] - In Spring Boot Dependencies, select:
> Web > Spring Web: REST APIs developemnt.
> Developer Tools > Spring Boot DevTools: Automatically restarts your application after save.
> Developer Tools > Lombok: Eliminates repetitive Java code by automatically generating getters, setters, constructors and annotations.
[5] - Click Create button.

## PROJECT STRUCTURE
[1] - The main folder is the src, which contains subfolders as main and test.
> Development files are created into src
> Tests are created into test

[2] - The simple structure is:
src/main/java/com.example.todolistapi/
|- controllers
|  |- TodoController.java
|- model
|  |- TodoItem.java
|- service
|  |- TodoService.java
|- TodoListApiApplication.java

## CREATING A MODEL
Given you are using Lombok dependency, you can use annotations to avoid the needing to create getting and setters.

```java
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
```

# CREATING A SERVICE
In the following example, we do not have a class to perform database queries, so the records are stored in-memory directly into the service.

Following Spring patter, add the annotation @Service in the class.

The annotation @Service defines the class as a holder of business rules and automatically handle the object life cycle and dependency injection to the controller.

By default, @Service makes Spring defines the instance of the class as singleton. You can change this default behavior by using the @Service annotation combined to the @Scope annotation.

Scope Annotation:
- How to Use:
  - @Scope("<type>")
- Types:
  - prototype: behaves like transient -> a new instance for every single request or injection.
  - request: behaves like scoped, a new instance per HTTP request.
  - session: the spring creates a new instance per HTTP session (tied to a specific user log-in ssession).

```java
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
```

## CREATING A CONTROLLER
Given you are using Spring, add the required annotations to the class and methods.

Annotations
- @RestController: defines the class as a web controller that can receives API Requests and automatically converts java objects to json (an vice-versa).
- @RequestMapping("/api/todos"): sets the base web address (url) for the controller.
- @GetMapping, @PostMapping, PutMapping and DeleteMapping: Map incomming HTTP requests to the specific method. It also allows you to defined a sub route to the endpoint for scenarios you have more than you endpoint for the same HTTP request.
- PathVariable: map a value from the route path.
- @RequestParam: automatically retrieve the json property with the same name as the method parameter name.
- @RequestBody: maps the entity request body to the object as param.


```java
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
```

## HOW TO DEFINE THE PORT IN WHICH THE SERVER WILL RUNS?
As default, the server port used by spring is 8000.

To modify it, open the `application.properties` file located at: src/main/resources directory.

Then, add the following line of code there to set the server port as 9000.

```
server.port = 9000
```

## HOW TO RUN THE SERVER USING HTTPS
[1] - Generate a Self-Assigned Certificated
- keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365

The process will asked you for a password. Enter a value as: 123

[2] - Copy the `keystore.p12` file from the directory in which you ran the commend to src/main/resources folder of your project, right next to the application.properties file.

[3] - In the application.properties file, add the following lines:

```
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=<PASSWORD_DEFINED>
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=springboot
```

[4] - In the gitignore file, add the following line to specify to certificate file be ignored.

```
### Certificate ===
keystore.p12
```

[5] - If your server is running, restart it.

## HOW TO RUN THE APPLICATION
To run the application, you need to be in an executable file, as TodoListApiApplication.java

## HOW TO TEST THE API
GetAll:
- curl -X GET https://localhost:9000/api/todos --insecure

GetById
- curl -X GET https://localhost:9000/api/todos/2 --insecure

Create
- curl -X POST https://localhost:9000/api/todos -d "description=Some Todo Item" --insecure

Complete
- curl -X PUT https://localhost:9000/api/todos/3/complete --insecure

Delete
- curl -X DELETE https://localhost:9000/api/todos/3 --insecure

