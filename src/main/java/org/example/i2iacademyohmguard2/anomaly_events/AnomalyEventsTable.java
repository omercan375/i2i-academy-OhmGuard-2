package org.example.i2iacademyohmguard2.anomaly_events;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.appliances.AppliancesTable;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "anomaly_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnomalyEventsTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private HomesTable home;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appliance_id")
    private AppliancesTable appliance;
    @Column(name = "measured_watt")
    private BigDecimal measuredWatt;
    @Column(name = "safe_watt_limit")
    private BigDecimal safeWattLimit;
    @Column(name = "breach_count")
    private int breachCount;
    @CreationTimestamp
    @Column(name = "detected_at")
    private LocalDateTime detectedAt;
}
