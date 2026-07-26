package org.example.i2iacademyohmguard2.homes.dto;

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
public class UpdateTariffLimits {
    @NotNull(message = "home is required")
    private UUID homeId;
    @NotNull(message = "normal tariff rate is required")
    @Positive
    private BigDecimal normTariffRate;
    @NotNull(message = "penalty tariff rate is required")
    @Positive
    private BigDecimal penaltyTariffRate;
}
