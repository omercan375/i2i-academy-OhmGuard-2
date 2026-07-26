package org.example.i2iacademyohmguard2.quota_events.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotaHistoryDto{
    private int thresholdPercentage;
    private BigDecimal budgetLimit;
    private BigDecimal accumulatedCost;
    private LocalDateTime createdAt;
}

