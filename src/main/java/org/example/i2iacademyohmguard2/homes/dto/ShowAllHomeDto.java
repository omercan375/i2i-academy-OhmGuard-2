package org.example.i2iacademyohmguard2.homes.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowAllHomeDto {
    private UUID homeId;
    private String name;
    private boolean isActive;
}
