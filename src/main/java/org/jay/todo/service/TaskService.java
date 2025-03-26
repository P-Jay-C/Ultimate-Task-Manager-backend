package org.jay.todo.service;

import lombok.RequiredArgsConstructor;
import org.jay.todo.dto.PagedTaskResponseDTO;
import org.jay.todo.dto.TaskDTO;
import org.jay.todo.entity.Task;
import org.jay.todo.entity.User;
import org.jay.todo.exception.ResourceNotFoundException;
import org.jay.todo.exception.UnauthorizedException;
import org.jay.todo.mapper.TaskMapper;
import org.jay.todo.repository.TaskRepository;
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

    public PagedTaskResponseDTO findTasksByOwner(User owner, int page, int size, String category, Boolean completed, String search, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir != null && sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "dueDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskRepository.findTasksByOwnerWithFiltersAndSearch(owner, category, completed, search, pageable);
        return taskMapper.toPagedTaskResponseDTO(taskPage);
    }

    public TaskDTO update(Long id, TaskDTO taskDTO, User user) {
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

        existingTask = tagService.setTags(existingTask,taskDTO.getTags());
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
}