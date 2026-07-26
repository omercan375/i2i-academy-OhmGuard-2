package org.example.i2iacademyohmguard2.kafka.registration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Asset registration topic'inde tasinan yasam dongusu mesaji.
 *
 * Ev kaydi ile cihaz kaydi ayri akislardir: ev olusturuldugunda henuz cihazi
 * yoktur, cihazlar sonradan tek tek eklenir. Bu nedenle mesaj tek bir cihaz
 * tasir; kullanilmayan alanlar null gelir:
 *
 *   HOME_CREATED      -> yalnizca homeId
 *   APPLIANCE_ADDED   -> homeId + applianceId + applianceName + safeWattLimit
 *   APPLIANCE_REMOVED -> homeId + applianceId
 */
public record AssetRegistrationEvent(
        RegistrationEventType eventType,
        UUID homeId,
        UUID applianceId,
        String applianceName,
        BigDecimal safeWattLimit,
        Instant occurredAt
) {

    public static AssetRegistrationEvent homeCreated(UUID homeId) {
        return new AssetRegistrationEvent(
                RegistrationEventType.HOME_CREATED,
                homeId, null, null, null, Instant.now());
    }

    public static AssetRegistrationEvent applianceAdded(
            UUID homeId, UUID applianceId, String applianceName, BigDecimal safeWattLimit) {
        return new AssetRegistrationEvent(
                RegistrationEventType.APPLIANCE_ADDED,
                homeId, applianceId, applianceName, safeWattLimit, Instant.now());
    }

    public static AssetRegistrationEvent applianceRemoved(UUID homeId, UUID applianceId) {
        return new AssetRegistrationEvent(
                RegistrationEventType.APPLIANCE_REMOVED,
                homeId, applianceId, null, null, Instant.now());
    }
}