package org.jay.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedTaskResponseDTO {
    private List<TaskDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}