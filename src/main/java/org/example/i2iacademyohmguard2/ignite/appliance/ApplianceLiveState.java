package org.example.i2iacademyohmguard2.ignite.appliance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Bir cihazin Apache Ignite'ta tutulan canli durumu (key = applianceId).
 *
 * Bu gorevde yalnizca son olcum alanlari (lastMeasuredWatt, lastMeasuredAt,
 * updatedAt) telemetri ile guncellenir. safeWattLimit / consecutiveBreachCount /
 * anomalous alanlari ileride anomali & kota mantigi tarafindan kullanilmak uzere
 * burada yer alir; telemetri guncellemesi bu alanlara DOKUNMAZ (mevcut degerleri
 * korunur).
 *
 * Ignite cache degeri ve EntryProcessor uzerinden tasindigi icin Serializable'dir.
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplianceLiveState implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID applianceId;
    private UUID homeId;
    private String name;

    private BigDecimal lastMeasuredWatt;
    private Instant lastMeasuredAt;
    private Instant updatedAt;

    // --- Bu gorevin kapsami disindaki, korunmasi gereken alanlar ---
    private BigDecimal safeWattLimit;
    private int consecutiveBreachCount;
    private boolean anomalous;
}
