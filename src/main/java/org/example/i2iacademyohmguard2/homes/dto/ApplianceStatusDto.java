package org.example.i2iacademyohmguard2.homes.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplianceStatusDto {
    private UUID applianceId;
    private String name;
    private BigDecimal lastMeasuredWatt;
    private BigDecimal safeWattLimit;
    private int consecutiveBreachCount;
    private boolean anomalous;
    private Instant lastMeasuredAt;
}
