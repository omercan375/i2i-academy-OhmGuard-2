package org.example.i2iacademyohmguard2.appliances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddAppliancesDto {
    @NotNull
    private UUID homeId;
    @NotBlank(message = "name is required")
    @Size(min = 2,max = 150,message = "name must be between 2 and 150")
    private String name;
    @NotNull(message = "safe watt limit must not be null")
    @Positive
    private BigDecimal safeWattLimit;
    private boolean active;
}
