package org.example.i2iacademyohmguard2.consumption_snapshots.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyConsumptionSnapshotsHistoryDto {
    private BigDecimal energyKwh;
    private BigDecimal totalCost;
    private LocalDateTime recordedAt;
}
