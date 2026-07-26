package org.example.i2iacademyohmguard2.billing_accounts.dto;


import lombok.*;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillingAccountInfos {
    private BigDecimal accumulatedEnergyKwh;
    private BigDecimal accumulatedCost;
    private ActiveTariff activeTariff;
}
