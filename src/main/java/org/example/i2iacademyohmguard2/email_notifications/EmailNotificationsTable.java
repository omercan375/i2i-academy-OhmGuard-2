package org.example.i2iacademyohmguard2.email_notifications;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.ai_recommendations.AiRecommendationsTable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailNotificationsTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id")
    private AiRecommendationsTable recommendations;
    @Column(name = "recipient_email")
    private String recipientEmail;
    @Column(name = "status")
    private String status;
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    @Column(name = "failure_reason")
    private String failureReason;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
