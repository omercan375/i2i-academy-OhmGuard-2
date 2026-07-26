package org.example.i2iacademyohmguard2.consumption_snapshots;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consumption_snapshots")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsumptionSnapshotsTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private HomesTable home;
    @Column(name = "energy_kwh")
    private BigDecimal energyKwh;
    @Column(name = "total_cost")
    private BigDecimal totalCost;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ConsumptionSnapshotsType type;
    @CreationTimestamp
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;


}
