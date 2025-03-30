package org.jay.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private String category;
    private boolean completed;
    private String updatedAt;
    private String createdAt;
    private String userId;
    private Set<String> tags;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, ARCHIVED
    private int progress; // 0-100
}