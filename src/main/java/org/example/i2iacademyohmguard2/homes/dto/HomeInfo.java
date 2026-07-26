package org.example.i2iacademyohmguard2.homes.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeInfo {
    private String name;
    private String contactEmail;
    private BigDecimal budgetLimit;
    private BigDecimal normalTariffRate;
    private BigDecimal penaltyTariffRate;
    private boolean isActive;
}
