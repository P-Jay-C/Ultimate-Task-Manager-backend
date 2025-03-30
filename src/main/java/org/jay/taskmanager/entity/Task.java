package org.jay.taskmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jay.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String priority; // "LOW", "MEDIUM", "HIGH"
    private String category;
    private boolean completed;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'PENDING'")
    private TaskStatus status = TaskStatus.PENDING; // Default to PENDING
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int progress = 0; // 0-100, default to 0
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"tasks", "roles", "password"})
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}