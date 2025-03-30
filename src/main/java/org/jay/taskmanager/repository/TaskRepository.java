package org.jay.taskmanager.repository;

import org.jay.taskmanager.entity.Task;
import org.jay.taskmanager.entity.User;
import org.jay.taskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(User owner);

    @Query("SELECT t FROM Task t WHERE t.owner = :owner " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:completed IS NULL OR t.completed = :completed) " +
            "AND (:search IS NULL OR t.title LIKE %:search% OR t.description LIKE %:search%)")
    Page<Task> findTasksByOwnerWithFiltersAndSearch(
            @Param("owner") User owner,
            @Param("category") String category,
            @Param("completed") Boolean completed,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.completed = false " +
            "AND t.dueDate BETWEEN :now AND :dueThreshold")
    List<Task> findTasksDueSoon(@Param("now") LocalDateTime now,
                                @Param("dueThreshold") LocalDateTime dueThreshold);

    Page<Task> findAll(Pageable pageable);

    Page<Task> findByOwnerAndStatus(User owner, TaskStatus status, Pageable pageable);

    Page<Task> findByOwnerAndStatusAndCategory(User owner, TaskStatus status, String category, Pageable pageable);
    Page<Task> findByOwnerAndStatusAndTitleContaining(User owner, TaskStatus status, String search, Pageable pageable);
    Page<Task> findByOwnerAndStatusAndCategoryContainingAndTitleContaining(
            User owner, TaskStatus status, String category, String search, Pageable pageable);
}