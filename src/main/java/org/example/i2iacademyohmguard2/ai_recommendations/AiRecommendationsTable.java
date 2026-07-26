package org.example.i2iacademyohmguard2.ai_recommendations;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.i2iacademyohmguard2.appliances.AppliancesTable;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * {@code ai_recommendations} tablosunun JPA karsiligi.
 *
 * home_id ve appliance_id iliskisel entity yerine dogrudan UUID olarak tutulur;
 * cunku bu kayit, canli state'ten (Ignite) gelen kimliklerle async olarak
 * olusturulur ve iliskili entity'lerin yuklenmesine ihtiyac yoktur. Kota
 * alarmlarinda appliance_id null'dir, anomalide dolar.
 */
@Entity
@Table(name = "ai_recommendations")
@Getter
@Setter
@NoArgsConstructor
public class AiRecommendationsTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private HomesTable home;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appliance_id")
    private AppliancesTable appliance;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 30)
    private TriggerType triggerType;

    @Column(name = "recommendation_text", columnDefinition = "text", nullable = false)
    private String recommendationText;

    @CreationTimestamp
    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt;
}
