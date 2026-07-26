package org.example.i2iacademyohmguard2.email_notifications.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private String homeName;
    private String triggerType;
    private String recipientEmail;
    private String status;
    private String text;
    private LocalDateTime createdAt;
}
