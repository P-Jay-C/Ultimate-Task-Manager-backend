package org.jay.taskmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class SuccessResponse {
    private Instant timestamp;
    private int status;
    private String message;
    private Object data;

    public SuccessResponse(int status, String message,Object data) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public SuccessResponse(int status, String message) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
    }
}
