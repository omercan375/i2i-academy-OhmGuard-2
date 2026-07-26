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
public class UpdateBudgetLimit {
    @NotNull(message = "home is required")
    private UUID homeId;
    @NotNull(message = "new budget limit is required")
    @Positive(message = "budget limit must be positive")
    private BigDecimal newBudgetLimit;

}
