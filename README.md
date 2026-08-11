# JAVA

## CREATING A WEB API PROJECT
1. Open IntelliJ
2. Click New Project
3. Enter
- Project Name 
- Select Maven
4. In Spring Boot Dependencies, select:
- **Web > Spring Web**: REST APIs development.
- **Developer Tools > Spring Boot DevTools**: Automatically restarts your application after save.
- **Developer Tools > Lombok**: Eliminates repetitive Java code by automatically generating getters, setters, constructors and annotations.
5. Click Create button.

## PROJECT STRUCTURE
1. The main folder is the `src`, which contains subfolders as `main` and `test`. 
- Development files are created into src 
- Tests are created into test

2. The simple structure is:
```code
src/main/java/com.example.todolistapi/
|- controllers
|  |- TodoController.java
|- entity
|  |- TodoItem.java
|- model
|  |- TodoItemInputModel.java
|- repository
|  |- TodoItemRepository.java
|- service
|  |- TodoService.java
|- TodoListApiApplication.java
```

## EXTRA DEPENDENCIES

### Dependencies

For connect your Web Api to a database, like SQL Server, your project requires more dependencies, like:
- JPA/Hibernate: works like Entity Framework in C# .NET.
- SqlServer: driver for SQL Server.

To add it to your project, you can open `pom.xml` file and add the following dependencies into `<dependencies>` tag:

```code
<!-- Hibernate and Spring Data JPA -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Microsoft SQL Server JDBC Driver -->
<dependency>
  <groupId>com.microsoft.sqlserver</groupId>
  <artifactId>mssql-jdbc</artifactId>
  <scope>runtime</scope>
</dependency>
```

Note the `scope runtime` in the SQL Server Driver, it is used to specify that this dependency is not required for building, but for runtime.

You may need to reload Maven to force the new dependencies to be downloaded.

### Configurations

You need also to configure some properties related to these dependencies in the `application.properties` file, like database access and JPA/Hibernate capabilities:

```code
spring.datasource.url=jdbc:sqlserver://YOUR_SERVER_IP:1433;databaseName=TodoDatabase;encrypt=true;trustServerCertificate=true;
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD

spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Observations:
- If you are running locally, you can change YOUR_SERVER_IP by `localhost`.
- If you are using a database first approach, prefer to set `spring.jpa.hibernate.ddl-auto` as `none` or `validate`. 
- If you are using a code-first approach, use `update` option.

## CREATING AN ENTITY

Given you are using JPA/Hibernate, you can use annotation to specify the relationship between your code entity and your database table.
- @Entity 
- @Table(name = "YOUR_DATABASE_TABLE_NAME")
- @Id 
- @GeneratedValue(strategy = GenerationType.IDENTITY)
- @Column(name = "YOUR_DATABASE_COLUMN_NAME", nullable = YOUR_DATABASE_TABLE_NULLABLE_BOOLEAN_VALUE)

You also need to create a default (empty) constructor so JPA/Hibernate can instantiate objects of this class type. Similar what is done for C# .NET applications.

Given you are using Lombok dependency, you can use annotations to avoid the needing to manually write getter and setter methods.
- @Getter 
- @Setter

```java
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

```

## CREATING A REPOSITORY
JPA/Hibernate allows you to perform database operations (SELECT/INSERT/UPDATE/DELETE) without the needing to write SQL commands.

By default, a repository the extends from `JpaRepository<T, D>` has already methods like:
- findAll
- findById
- existsById
- save: to automatically save (insert/update) an entry
- deleteById

Besides, it also allows you to create custom methods by just adding the method declaration and the SQL Command as annotation.

```java
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
```

If you need a query that involves extra logic, like IF/ELSE, you can create a new interface for a customRepository without add any annotation. 

Then, you must create the repository class. The class must have a property of `EntityManager` type with a `@PersistenceContext` annotation. All methods must have the `@Override` annotation.

Class "body" example (not used in this project):

```java
@PersistenceContext
private EntityManager entityManager;

@Override
public List<TodoItem> findUsingComplexLogic(string filterText) {
  String query = "SELECT t FROM TODOITEM t WHERE t.DESCRIPTION LIKE :pFilterText";

  if (filterText.equalsIgnoreCase("urgent)) {
    query += " AND t.COMPLETED = false";
  }

  return this.entityManager
    .createQuery(query, TodoItem.class)
    .setParameters("pFilterText", "%" + filterText + "%")
    .getResultList();
}
```

To make it available for use, the best approach is to modify your main repository to extend both from jpa and the new custom one.

## CREATING A SERVICE
Following Spring patter, add the annotation @Service in the class.

**What is @Service**: The annotation @Service defines the class as a holder of business rules and automatically handle the object life cycle and dependency injection to the controller.

**Life Cycle**: By default, @Service makes Spring defines the instance of the class as singleton. You can change this default behavior by using the @Service annotation combined to the @Scope annotation.

Scope Annotation:
- How to Use:
  - @Scope("<type>")
- Types:
  - prototype: behaves like transient, a new instance for every single request or injection.
  - request: behaves like scoped, a new instance per HTTP request.
  - session: the spring creates a new instance per HTTP session (tied to a specific user log-in ssession).

Different from C# .NET, that requires services, queries and repositories to be scoped because of `DbContext` is declared internally as scoped, Java use this classes' instance as singleton to optimize performance.

```java
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

```

## CREATING A CONTROLLER
Given you are using Spring, add the required annotations to the class and methods.

Annotations
- @RestController: defines the class as a web controller that can receives API Requests and automatically converts java objects to json (an vice-versa).
- @RequestMapping("/api/todos"): sets the base web address (url) for the controller.
- @GetMapping, @PostMapping, @PutMapping and @DeleteMapping: Map incomming HTTP requests to the specific method. It also allows you to define a sub route to the endpoint for scenarios you have more than you endpoint for the same HTTP request.
- @PathVariable: map a value from the route path.
- @RequestParam: automatically retrieve the json property with the same name as the method parameter name.
- @RequestBody: maps the entity request body to the object as param.

```java
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
```

## HOW TO SPECIFY THE SERVER PORT IN WHICH THE APPLICATION RUNS?
As default, the server port used by spring is 8000.

To modify it, open the `application.properties` file located at: src/main/resources directory.

Then, add the following line of code there to set the server port as 9000.

```code
server.port = 9000
```

## HOW TO RUN THE SERVER USING HTTPS
1. Generate a Self-Assigned Certificated by using a terminal:

```code
keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365
```

The process will ask you for a password. Enter a value as: `123`.

2. Copy the `keystore.p12` file from the directory in which you ran the commend to src/main/resources folder of your project, right next to the application.properties file.

3. In the application.properties file, add the following lines:

```code
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=<PASSWORD_DEFINED>
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=springboot
```

4. In the gitignore file, add the following line to specify to certificate file be ignored.

```code
### Certificate ===
keystore.p12
```

## HOW TO CONFIGURE SQL SERVER

By default, SQL Server does not support TCP/IP, so Java SQL Server Driver can not sucessfully connect to the database server.

To configure it, you can follow the steps below:
1. Open SQL Server Configuration Manager.
- In Windows, search by `SQLServerManager16.msc` depending on SQL Server version.
2. Enable TCP/IP: 
- Go to SQL Server Network Configuration > Protocols for MSSQLSERVER (or your instance name). Right-click TCP/IP and choose Enable.
3. Set the Port: 
- Right-click TCP/IP again, select Properties, and go to the IP Addresses tab. Scroll down to `IPAll` and verify that TCP Port is set to 1433.
4. Enable SQL Authentication: 
- Open SQL Server Management Studio (SSMS), right-click your Server Instance > Properties > Security, and select SQL Server and Windows Authentication mode.
5. Restart Server: 
- Restart the SQL Server service from your Windows Services panel to apply changes.

## HOW TO RUN THE APPLICATION
To run the application, you need to be in an executable file, as TodoListApiApplication.java

## HOW TO TEST THE API
Get all records:
```code
curl -X GET https://localhost:9000/api/todos --insecure
```

Get all incomplete records:
```code
curl -X GET https://localhost:9000/api/todos?onlyincomplete=true --insecure
```

Get a record by id:
```code
curl -X GET https://localhost:9000/api/todos/2 --insecure
```

Create a new record:
```code
curl -X POST https://localhost:9000/api/todos -H "Content-Type: application/json" -d "{\"description\":\"Test Todo Item\", \"completed\":false}" --insecure
```

Complete an existent record:
```code
curl -X PUT https://localhost:9000/api/todos/3/complete --insecure
```

Delete a record:
```code
curl -X DELETE https://localhost:9000/api/todos/3 --insecure
```
