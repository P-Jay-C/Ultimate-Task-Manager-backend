package org.jay.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedUserResponseDTO {
    private List<UserDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}