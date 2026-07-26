package org.example.i2iacademyohmguard2.homes.dto;

import lombok.*;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeStatusDto {
    private UUID homeId;
    private BigDecimal accumulatedKwh;
    private BigDecimal accumulatedCost;
    private BigDecimal budgetLimit;
    private ActiveTariff activeTariff;
    private boolean alert80Sent;
    private boolean alert100Sent;
    private List<ApplianceStatusDto> appliances;
}
