package org.example.i2iacademyohmguard2.kafka;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Sensorden Kafka uzerinden gelen anlik telemetri mesaji.
 *
 * Bu record yalnizca veriyi tasir; hicbir domain hesabi (kWh, maliyet, kota,
 * anomali) icermez. intervalSeconds opsiyoneldir ve bu asamada yalnizca
 * deserialization sirasinda tolere edilmek icin bulunur, uzerinde hesap yapilmaz.
 */
public record TelemetryMessage(
        UUID homeId,
        UUID applianceId,
        BigDecimal measuredWatt,
        Instant measuredAt,
        Integer intervalSeconds
) {
    public TelemetryMessage {
        if (homeId == null) {
            throw new IllegalArgumentException("homeId null olamaz");
        }
        if (applianceId == null) {
            throw new IllegalArgumentException("applianceId null olamaz");
        }
        if (measuredWatt == null) {
            throw new IllegalArgumentException("measuredWatt null olamaz");
        }
        if (measuredWatt.signum() < 0) {
            throw new IllegalArgumentException("measuredWatt negatif olamaz");
        }
        if (measuredAt == null) {
            throw new IllegalArgumentException("measuredAt null olamaz");
        }
    }
}
