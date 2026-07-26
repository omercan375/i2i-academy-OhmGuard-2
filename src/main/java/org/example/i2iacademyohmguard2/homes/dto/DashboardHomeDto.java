package org.example.i2iacademyohmguard2.homes.dto;

import lombok.*;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardHomeDto {
    private UUID homeId;
    private String name;
    private String contactEmail;
    private BigDecimal budgetLimit;
    private BigDecimal accumulatedCost;
    private BigDecimal accumulatedKwh;
    private BigDecimal usagePercent;
    private ActiveTariff activeTariff;
    private boolean alert80Sent;
    private boolean alert100Sent;
    private long anomalyCount;
    private int applianceCount;
    private boolean live;
}
