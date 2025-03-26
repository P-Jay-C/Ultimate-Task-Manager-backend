package org.jay.todo.controller;

import lombok.RequiredArgsConstructor;
import org.jay.todo.dto.PagedTaskResponseDTO;
import org.jay.todo.dto.PagedUserResponseDTO;
import org.jay.todo.dto.TaskDTO;
import org.jay.todo.dto.UserDTO;
import org.jay.todo.exception.SuccessResponse;
import org.jay.todo.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // User Management
    @GetMapping("/users")
    public ResponseEntity<SuccessResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedUserResponseDTO users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Users retrieved successfully", users));
    }

    @PostMapping("/users")
    public ResponseEntity<SuccessResponse> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = adminService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse(HttpStatus.CREATED.value(), "User created successfully", createdUser));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<SuccessResponse> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = adminService.updateUser(id, userDTO);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "User updated successfully", updatedUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "User deleted successfully", null));
    }

    // Task Management
    @GetMapping("/tasks")
    public ResponseEntity<SuccessResponse> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedTaskResponseDTO tasks = adminService.getAllTasks(page, size);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Tasks retrieved successfully", tasks));
    }

    @PostMapping("/tasks")
    public ResponseEntity<SuccessResponse> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = adminService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse(HttpStatus.CREATED.value(), "Task created successfully", createdTask));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<SuccessResponse> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = adminService.updateTask(id, taskDTO);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task updated successfully", updatedTask));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<SuccessResponse> deleteTask(@PathVariable Long id) {
        adminService.deleteTask(id);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), "Task deleted successfully", null));
    }

}