package org.jay.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.jay.taskmanager.entity.Task;
import org.jay.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class TaskReminderScheduler {
    private static final Logger log = LoggerFactory.getLogger(TaskReminderScheduler.class);
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 3600000) // Every hour
    public void sendTaskReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueThreshold = now.plusHours(24);
        
        List<Task> tasksDueSoon = taskRepository.findTasksDueSoon(now, dueThreshold);
        log.info("Found {} tasks due soon", tasksDueSoon.size());
        
        for (Task task : tasksDueSoon) {
            try {
                notificationService.sendTaskReminderEmail(task); // Email
                notificationService.sendPushNotification(task);  // Push
                log.info("Sent reminders for task: {}", task.getTitle());
            } catch (Exception e) {
                log.error("Failed to send reminder for task: {} - {}", task.getTitle(), e.getMessage());
            }
        }
    }
}