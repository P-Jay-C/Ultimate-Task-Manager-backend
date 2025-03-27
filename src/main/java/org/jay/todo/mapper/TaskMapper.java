package org.jay.todo.mapper;

import org.jay.todo.dto.PagedTaskResponseDTO;
import org.jay.todo.dto.TaskDTO;
import org.jay.todo.entity.Tag;
import org.jay.todo.entity.Task;
import org.jay.todo.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    public TaskDTO toTaskDTO(Task task) {
        if (task == null) {
            return null;
        }
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate() != null ? task.getDueDate().toString() : null)
                .priority(task.getPriority())
                .category(task.getCategory())
                .completed(task.isCompleted())
                .updatedAt(task.getUpdatedAt() != null ? task.getUpdatedAt().toString() : null)
                .createdAt(task.getCreatedAt() != null ? task.getCreatedAt().toString() : null)
                .userId(task.getOwner() != null ? task.getOwner().getId().toString() : null)
                .tags(task.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .status(task.getStatus().name())
                .progress(task.getProgress())
                .build();
    }

    public Task toTaskEntity(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate() != null ? LocalDateTime.parse(taskDTO.getDueDate()) : null);
        task.setPriority(taskDTO.getPriority());
        task.setCategory(taskDTO.getCategory());
        task.setCompleted(taskDTO.isCompleted());
        if (taskDTO.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(taskDTO.getStatus()));
        }
        task.setProgress(taskDTO.getProgress());
        return task; // Tags handled in TagService
    }

    public PagedTaskResponseDTO toPagedTaskResponseDTO(Page<Task> taskPage) {
        if (taskPage == null) {
            return null;
        }
        List<TaskDTO> taskDTOs = taskPage.getContent().stream()
                .map(this::toTaskDTO)
                .collect(Collectors.toList());

        return PagedTaskResponseDTO.builder()
                .content(taskDTOs)
                .page(taskPage.getNumber())
                .size(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .build();
    }
}