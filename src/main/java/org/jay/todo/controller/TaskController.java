package org.jay.todo.controller;

import org.jay.todo.dto.PagedTaskResponseDTO;
import org.jay.todo.dto.TaskDTO;
import org.jay.todo.entity.User;
import org.jay.todo.enums.TaskStatus;
import org.jay.todo.exception.SuccessResponse;
import org.jay.todo.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
        logger.info("TaskController initialized");
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getTaskById(@PathVariable Long id) {
        logger.debug("Fetching task with ID: {}", id);
        TaskDTO taskDTO = taskService.getTaskById(id);
        return ResponseEntity.ok(
                new SuccessResponse(HttpStatus.OK.value(), "Task retrieved successfully", taskDTO)
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> createTask(@RequestBody TaskDTO taskDTO, @AuthenticationPrincipal User user) {
        logger.debug("Creating task for user: {}", user.getUsername());
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
        logger.debug("Fetching tasks for user: {}", user.getUsername());
        PagedTaskResponseDTO responseDTO = taskService.findTasksByOwner(user, page, size, category, completed, search, sortBy, sortDir);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Tasks retrieved successfully", responseDTO));
    }

    @GetMapping("/status")
    public ResponseEntity<SuccessResponse> getTasksByStatus(
            @RequestParam TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal User user) {
        logger.debug("Fetching tasks by status: {} for user: {}", status, user.getUsername());
        PagedTaskResponseDTO tasks = taskService.findTasksByOwnerAndStatus(user, status, page, size, category, search);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Tasks retrieved successfully", tasks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO,
                                                      @AuthenticationPrincipal User user) {
        logger.debug("Updating task with ID: {} for user: {}", id, user.getUsername());
        TaskDTO updatedTaskDTO = taskService.update(id, taskDTO, user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task updated successfully", updatedTaskDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        logger.debug("Deleting task with ID: {} for user: {}", id, user.getUsername());
        taskService.delete(id, user);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task deleted successfully", null));
    }

}