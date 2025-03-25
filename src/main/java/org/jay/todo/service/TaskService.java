package org.jay.todo.service;

import lombok.RequiredArgsConstructor;
import org.jay.todo.dto.PagedTaskResponseDTO;
import org.jay.todo.dto.TaskDTO;
import org.jay.todo.entity.Task;
import org.jay.todo.entity.User;
import org.jay.todo.exception.ResourceNotFoundException;
import org.jay.todo.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Task save(Task task, User user) {
        task.setOwner(user);
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Task not found : "+ id)
        );
    }

    public List<Task> findByOwner(User owner) {
        return taskRepository.findByOwner(owner);
    }

    public PagedTaskResponseDTO findTasksByOwner(User owner, int page, int size, String category, Boolean completed, String search, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir != null && sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "dueDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskRepository.findTasksByOwnerWithFiltersAndSearch(owner, category, completed, search, pageable);

        return PagedTaskResponseDTO.builder()
                .content(taskPage.getContent().stream()
                        .map(task -> TaskDTO.builder()
                                .id(task.getId())
                                .title(task.getTitle())
                                .description(task.getDescription())
                                .dueDate(task.getDueDate().toString())
                                .priority(task.getPriority())
                                .category(task.getCategory())
                                .completed(task.isCompleted())
                                .build())
                        .collect(Collectors.toList()))
                .page(taskPage.getNumber())
                .size(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .build();
    }

    public Task update(Long id, Task task, User user) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!existingTask.getOwner().equals(user)) {
            throw new RuntimeException("Unauthorized to update this task");
        }
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setCategory(task.getCategory());
        existingTask.setCompleted(task.isCompleted());
        return taskRepository.save(existingTask);
    }

    public void delete(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getOwner().equals(user)) {
            throw new RuntimeException("Unauthorized to delete this task");
        }
        taskRepository.delete(task);
    }
}