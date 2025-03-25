package org.jay.todo.service;

import lombok.RequiredArgsConstructor;
import org.jay.todo.dto.PushNotificationDTO;
import org.jay.todo.entity.Task;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendTaskReminderEmail(Task task) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(task.getOwner().getEmail());
        message.setSubject("Task Reminder: " + task.getTitle());
        message.setText("Dear " + task.getOwner().getUsername() + ",\n\n" +
                "This is a reminder for your task:\n" +
                "- Title: " + task.getTitle() + "\n" +
                "- Description: " + task.getDescription() + "\n" +
                "- Due Date: " + task.getDueDate() + "\n\n" +
                "Please complete it by the due date.\n\n" +
                "Best regards,\nUltimate Todo Team");
        mailSender.send(message);
    }

    public void sendPushNotification(Task task) {
        PushNotificationDTO notification = new PushNotificationDTO(
                "Task Reminder: " + task.getTitle(),
                "Due on " + task.getDueDate() + ": " + task.getDescription(),
                task.getId()
        );
        messagingTemplate.convertAndSend("/topic/task-notifications", notification);
    }
}