package org.example.i2iacademyohmguard2.appliances.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowAllAppliancesDto {
    private UUID applianceId;
    private String applianceName;
    private boolean isActive;
}
