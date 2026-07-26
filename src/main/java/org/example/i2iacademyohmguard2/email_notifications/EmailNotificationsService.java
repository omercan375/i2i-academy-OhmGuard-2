package org.example.i2iacademyohmguard2.email_notifications;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.ai_recommendations.AiRecommendationsTable;
import org.example.i2iacademyohmguard2.email_notifications.dto.NotificationDto;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailNotificationsService{

    private final EmailNotificationsRepo emailNotificationsRepo;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:ohmguard@ohmguard.local}")
    private String fromAddress;

    public void newEmail(AiRecommendationsTable aiRecommendationsTable){
        String recipient = aiRecommendationsTable.getHome().getContactEmail();
        EmailNotificationsTable emailNotificationsTable = EmailNotificationsTable.builder()
                .recommendations(aiRecommendationsTable)
                .recipientEmail(recipient)
                .status("PENDING")
                .build();
        try {
            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(recipient);
                message.setSubject("Ohm Guard Enerji Bildirimi");
                message.setText(aiRecommendationsTable.getRecommendationText());
                mailSender.send(message);
                emailNotificationsTable.setStatus("SENT");
                emailNotificationsTable.setSentAt(LocalDateTime.now());
            }
        } catch (Exception e) {
            emailNotificationsTable.setStatus("FAILED");
            emailNotificationsTable.setFailureReason(e.getMessage());
        }
        emailNotificationsRepo.save(emailNotificationsTable);
    }

    public List<NotificationDto> recentNotifications() {
        return toDtos(emailNotificationsRepo.findRecent(PageRequest.of(0, 50)));
    }

    public List<NotificationDto> recentNotificationsForUser(UsersTable user) {
        return toDtos(emailNotificationsRepo.findRecentByOwner(user.getId(), PageRequest.of(0, 50)));
    }

    private List<NotificationDto> toDtos(List<EmailNotificationsTable> rows) {
        List<NotificationDto> list = new ArrayList<>();
        for (EmailNotificationsTable e : rows) {
            AiRecommendationsTable r = e.getRecommendations();
            list.add(NotificationDto.builder()
                    .homeName(r.getHome().getHomeName())
                    .triggerType(r.getTriggerType().name())
                    .recipientEmail(e.getRecipientEmail())
                    .status(e.getStatus())
                    .text(r.getRecommendationText())
                    .createdAt(e.getCreatedAt())
                    .build());
        }
        return list;
    }
}
