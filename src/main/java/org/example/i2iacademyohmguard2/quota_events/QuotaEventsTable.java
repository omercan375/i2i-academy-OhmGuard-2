package org.example.i2iacademyohmguard2.quota_events;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quota_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotaEventsTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private HomesTable home;
    @Column(name = "threshold_percentage")
    private int thresholdPercentage;
    @Column(name = "budget_limit")
    private BigDecimal budgetLimit;
    @Column(name = "accumulated_cost")
    private BigDecimal accumulatedCost;
    @CreationTimestamp
    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;


}


