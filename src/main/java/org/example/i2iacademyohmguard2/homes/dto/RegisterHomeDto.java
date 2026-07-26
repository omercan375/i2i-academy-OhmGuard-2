package org.example.i2iacademyohmguard2.homes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterHomeDto {
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 150, message = "name must be between 2 and 150")
    private String name;
    @Email(message = "please enter available email")
    @NotBlank(message = "contact email is required")
    @Size(min = 5, max = 255, message = "contact email must be between 5 and 255")
    private String contactEmail;
    @NotNull(message = "budget limit must not be null")
    @Positive(message = "budget limit must be positive")
    private BigDecimal budgetLimit;
    @NotNull(message = "normal tariff rate must not be null")
    @Positive(message = "normal tariff rate must be positive")
    private BigDecimal normalTariffRate;
    @NotNull(message = "penalty tariff rate must not be null")
    @Positive(message = "penalty tariff rate must be positive")
    private BigDecimal penaltyTariffRate;
    @Valid
    private List<RegisterApplianceDto> appliances;
}
