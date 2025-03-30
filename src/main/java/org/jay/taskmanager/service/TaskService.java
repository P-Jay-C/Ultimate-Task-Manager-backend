package org.jay.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.jay.taskmanager.dto.PagedTaskResponseDTO;
import org.jay.taskmanager.dto.TaskDTO;
import org.jay.taskmanager.entity.Task;
import org.jay.taskmanager.entity.User;
import org.jay.taskmanager.enums.TaskStatus;
import org.jay.taskmanager.exception.ResourceNotFoundException;
import org.jay.taskmanager.exception.UnauthorizedException;
import org.jay.taskmanager.mapper.TaskMapper;
import org.jay.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    private final TaskMapper taskMapper;
    private final TagService tagService;

    public TaskDTO save(TaskDTO taskDTO, User user) {
        validateProgress(taskDTO.getProgress());
        Task task = taskMapper.toTaskEntity(taskDTO);
        task.setOwner(user);
        task = tagService.setTags(task, taskDTO.getTags());
        Task savedTask = taskRepository.save(task);
        checkAndSendReminder(savedTask);
        return taskMapper.toTaskDTO(savedTask);
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        return taskMapper.toTaskDTO(task);
    }

    public PagedTaskResponseDTO findTasksByOwner(User owner, int page, int size, String category, Boolean completed,
                                                 String search, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir != null && sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "dueDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskRepository.findTasksByOwnerWithFiltersAndSearch(owner, category, completed, search, pageable);
        return taskMapper.toPagedTaskResponseDTO(taskPage);
    }

    public PagedTaskResponseDTO findTasksByOwnerAndStatus(User owner, TaskStatus status, int page, int size, String category, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage;

        if (category != null && search != null) {
            taskPage = taskRepository.findByOwnerAndStatusAndCategoryContainingAndTitleContaining(owner, status, category, search, pageable);
        } else if (category != null) {
            taskPage = taskRepository.findByOwnerAndStatusAndCategory(owner, status, category, pageable);
        } else if (search != null) {
            taskPage = taskRepository.findByOwnerAndStatusAndTitleContaining(owner, status, search, pageable);
        } else {
            taskPage = taskRepository.findByOwnerAndStatus(owner, status, pageable);
        }

        return taskMapper.toPagedTaskResponseDTO(taskPage);
    }

    public TaskDTO update(Long id, TaskDTO taskDTO, User user) {
        validateProgress(taskDTO.getProgress());
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!existingTask.getOwner().equals(user)) {
            throw new UnauthorizedException("Unauthorized to update this task");
        }
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setDueDate(taskDTO.getDueDate() != null ? LocalDateTime.parse(taskDTO.getDueDate()) : null);
        existingTask.setPriority(taskDTO.getPriority());
        existingTask.setCategory(taskDTO.getCategory());
        existingTask.setCompleted(taskDTO.isCompleted());
        if (taskDTO.getStatus() != null) {
            TaskStatus newStatus = TaskStatus.valueOf(taskDTO.getStatus());
            existingTask.setStatus(newStatus);
            if (newStatus == TaskStatus.COMPLETED) {
                existingTask.setProgress(100);
                existingTask.setCompleted(true);
            }
            else {
                existingTask.setCompleted(false);
            }
        }
        existingTask.setProgress(taskDTO.getProgress());
        existingTask = tagService.setTags(existingTask, taskDTO.getTags());
        Task updatedTask = taskRepository.save(existingTask);
        checkAndSendReminder(updatedTask);
        return taskMapper.toTaskDTO(updatedTask);
    }

    public void delete(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getOwner().equals(user)) {
            throw new UnauthorizedException("Unauthorized to delete this task");
        }
        taskRepository.delete(task);
    }

    private void checkAndSendReminder(Task task) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueThreshold = now.plusHours(24);
        if (!task.isCompleted() && task.getDueDate() != null &&
                task.getDueDate().isAfter(now) && task.getDueDate().isBefore(dueThreshold)) {
            notificationService.sendTaskReminderEmail(task);
            notificationService.sendPushNotification(task);
        }
    }

    private void validateProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
    }
}