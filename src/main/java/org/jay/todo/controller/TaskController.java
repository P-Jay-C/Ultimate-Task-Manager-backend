package org.jay.todo.controller;

import org.jay.todo.dto.PagedTaskResponseDTO;
import org.jay.todo.dto.TaskDTO;
import org.jay.todo.entity.User;
import org.jay.todo.exception.SuccessResponse;
import org.jay.todo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getTaskById(@PathVariable Long id) {
        TaskDTO taskDTO = taskService.getTaskById(id);
        return ResponseEntity.ok(
                new SuccessResponse(HttpStatus.OK.value(), "Task retrieved successfully", taskDTO)
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> createTask(@RequestBody TaskDTO taskDTO, @AuthenticationPrincipal User user) {
        TaskDTO createdTaskDTO = taskService.save(taskDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse(HttpStatus.CREATED.value(), "Task created successfully", createdTaskDTO));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse> getTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedTaskResponseDTO responseDTO = taskService.findTasksByOwner(user, page, size, category, completed, search, sortBy, sortDir);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Tasks retrieved successfully", responseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO,
                                                      @AuthenticationPrincipal User user) {
        TaskDTO updatedTaskDTO = taskService.update(id, taskDTO, user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task updated successfully", updatedTaskDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        taskService.delete(id, user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task deleted successfully", null));
    }
}