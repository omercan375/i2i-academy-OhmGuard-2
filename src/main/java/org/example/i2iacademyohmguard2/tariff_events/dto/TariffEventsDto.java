package org.example.i2iacademyohmguard2.tariff_events.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffEventsDto {
    private String previousTariff;
    private String newTariff;
    private LocalDateTime activedAt;
}
