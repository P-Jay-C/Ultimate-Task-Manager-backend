package org.jay.todo.repository;

import org.jay.todo.entity.Task;
import org.jay.todo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}