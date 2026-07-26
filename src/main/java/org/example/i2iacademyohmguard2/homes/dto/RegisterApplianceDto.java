package org.example.i2iacademyohmguard2.homes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterApplianceDto {
    @NotBlank(message = "appliance name is required")
    @Size(min = 2, max = 150, message = "appliance name must be between 2 and 150")
    private String name;
    @NotNull(message = "safe watt limit must not be null")
    @Positive(message = "safe watt limit must be positive")
    private BigDecimal safeWattLimit;
}
