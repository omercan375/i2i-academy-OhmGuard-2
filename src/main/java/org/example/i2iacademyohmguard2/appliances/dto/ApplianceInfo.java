package org.example.i2iacademyohmguard2.appliances.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplianceInfo {
    private String applianceName;
    private BigDecimal safeWattLimit;
    private boolean active;
}
