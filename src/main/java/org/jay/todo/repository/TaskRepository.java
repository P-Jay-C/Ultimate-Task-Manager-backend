// TaskRepository.java
package org.jay.todo.repository;

import org.jay.todo.entity.Task;
import org.jay.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(User owner);
}