// TaskService.java
package org.jay.todo.service;

import org.jay.todo.entity.Task;
import org.jay.todo.entity.User;
import org.jay.todo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task save(Task task, User owner) {
        task.setOwner(owner);
        return taskRepository.save(task);
    }

    public List<Task> findByOwner(User owner) {
        return taskRepository.findByOwner(owner);
    }

    public Task update(Long id, Task task, User owner) {
        Task existing = taskRepository.findById(id).orElseThrow();
        if (!existing.getOwner().equals(owner)) {
            throw new SecurityException("Unauthorized");
        }
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setDueDate(task.getDueDate());
        existing.setPriority(task.getPriority());
        existing.setCategory(task.getCategory());
        existing.setCompleted(task.isCompleted());
        return taskRepository.save(existing);
    }

    public void delete(Long id, User owner) {
        Task task = taskRepository.findById(id).orElseThrow(
        );
        if (!task.getOwner().equals(owner)) {
            throw new SecurityException("Unauthorized");
        }
        taskRepository.delete(task);
    }
}