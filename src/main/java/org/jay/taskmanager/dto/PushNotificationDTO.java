package org.jay.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushNotificationDTO {
    private String title;
    private String message;
    private Long taskId;
}