package org.example.i2iacademyohmguard2.appliances.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSafeWattLimitDto {
    @NotNull(message = "appliance required")
    private UUID applianceId;
    @NotNull(message = "home required")
    private UUID homeId;
    @NotNull(message = "new safe watt limit required")
    @Positive
    private BigDecimal newWattLimit;

}
