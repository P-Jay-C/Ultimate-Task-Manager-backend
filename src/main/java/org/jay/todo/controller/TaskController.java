// TaskController.java
package org.jay.todo.controller;
import org.jay.todo.entity.Task;
import org.jay.todo.entity.User;
import org.jay.todo.exception.SuccessResponse;
import org.jay.todo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> createTask(@RequestBody Task task, @AuthenticationPrincipal User user) {
        Task createdTask = taskService.save(task, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse(HttpStatus.CREATED.value(), "Task created successfully", createdTask));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse> getTasks(@AuthenticationPrincipal User user) {
        List<Task> tasks = taskService.findByOwner(user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Tasks retrieved successfully", tasks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateTask(@PathVariable Long id, @RequestBody Task task,
                                                      @AuthenticationPrincipal User user) {
        Task updatedTask = taskService.update(id, task, user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task updated successfully", updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        taskService.delete(id, user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task deleted successfully", null));
    }
}