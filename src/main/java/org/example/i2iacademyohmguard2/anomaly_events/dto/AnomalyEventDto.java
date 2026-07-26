package org.example.i2iacademyohmguard2.anomaly_events.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnomalyEventDto {
    private UUID applianceId;
    private String applianceName;
    private BigDecimal measuredWatt;
    private BigDecimal safeWattLimit;
    private int breachCount;
    private LocalDateTime detectedAt;
}
