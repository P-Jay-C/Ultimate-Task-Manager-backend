package org.jay.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.jay.taskmanager.dto.PagedTaskResponseDTO;
import org.jay.taskmanager.dto.PagedUserResponseDTO;
import org.jay.taskmanager.dto.TaskDTO;
import org.jay.taskmanager.dto.UserDTO;
import org.jay.taskmanager.entity.Task;
import org.jay.taskmanager.entity.User;
import org.jay.taskmanager.enums.TaskStatus;
import org.jay.taskmanager.mapper.TaskMapper;
import org.jay.taskmanager.mapper.UserMapper;
import org.jay.taskmanager.repository.TaskRepository;
import org.jay.taskmanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;
    private final PasswordEncoder passwordEncoder;
    private final TagService tagService;

    // User Management (unchanged)
    public PagedUserResponseDTO getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userMapper.toPagedUserResponseDTO(userPage);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toUserEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // Task Management
    public PagedTaskResponseDTO getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findAll(pageable);
        return taskMapper.toPagedTaskResponseDTO(taskPage);
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        validateProgress(taskDTO.getProgress());
        User owner = userRepository.findById(Long.parseLong(taskDTO.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found for task owner"));
        Task task = taskMapper.toTaskEntity(taskDTO);
        task.setOwner(owner);
        task = tagService.setTags(task, taskDTO.getTags());
        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskDTO(savedTask);
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        validateProgress(taskDTO.getProgress());
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setDueDate(taskDTO.getDueDate() != null ? java.time.LocalDateTime.parse(taskDTO.getDueDate()) : null);
        existingTask.setPriority(taskDTO.getPriority());
        existingTask.setCategory(taskDTO.getCategory());
        existingTask.setCompleted(taskDTO.isCompleted());
        if (taskDTO.getStatus() != null) {
            TaskStatus newStatus = TaskStatus.valueOf(taskDTO.getStatus());
            existingTask.setStatus(newStatus);
            if (newStatus == TaskStatus.COMPLETED) {
                existingTask.setProgress(100);
            }
        }
        existingTask.setProgress(taskDTO.getProgress());
        if (taskDTO.getUserId() != null) {
            User owner = userRepository.findById(Long.parseLong(taskDTO.getUserId()))
                    .orElseThrow(() -> new RuntimeException("User not found for task owner"));
            existingTask.setOwner(owner);
        }
        existingTask = tagService.setTags(existingTask, taskDTO.getTags());
        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toTaskDTO(updatedTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    private void validateProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
    }
}