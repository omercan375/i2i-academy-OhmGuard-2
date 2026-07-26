package org.example.i2iacademyohmguard2.ignite.appliance;

import org.apache.ignite.cache.CacheEntryProcessor;
import org.example.i2iacademyohmguard2.ignite.appliance.dto.ApplianceProcessResponse;

import javax.cache.processor.MutableEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Bir cihazin son telemetri olcumunu ATOMIK olarak Ignite cache'ine yazan
 * EntryProcessor.
 *
 * cache.invoke(key, processor) cagrisi ayni anahtar icin Ignite tarafindan
 * kilitlenerek calistirildigindan, ayni applianceId'ye gelen es zamanli
 * mesajlar birbirini bozmaz (read-modify-write kaybi olmaz).
 *
 * Tek gorevi: mevcut state'i almak, yoksa minimum bir state olusturmak, son
 * watt/zaman alanlarini guncellemek ve cache'e geri yazmaktir. Baska hicbir
 * is kurali (kota, tarife, anomali, billing) burada calistirilmaz.
 *
 * Bayat mesaj korumasi: gelen olcumun zamani cache'teki son olcumden eskiyse
 * yazma yapilmaz. Boylece yeniden islenen veya geciken bir Kafka mesaji
 * cihazin "son durumunu" geriye goturemez.
 *
 * NOT: Kaydi olmayan bir cihazdan telemetri gelirse burada minimum bir state
 * olusturulur; bu state'in safeWattLimit alani NULL kalir (guvenli limit
 * yalnizca kayit/guncelleme akisindan gelir). Anomali kurallarini isleten
 * modul, safeWattLimit null iken degerlendirme yapmamalidir.
 */
public class LatestTelemetryProcessor
        implements CacheEntryProcessor<UUID, ApplianceLiveState, ApplianceProcessResponse> {

    private static final long serialVersionUID = 1L;

    private static final int BREACH_THRESHOLD = 3;

    private final UUID homeId;
    private final BigDecimal measuredWatt;
    private final Instant measuredAt;

    public LatestTelemetryProcessor(UUID homeId, BigDecimal measuredWatt, Instant measuredAt) {
        this.homeId = homeId;
        this.measuredWatt = measuredWatt;
        this.measuredAt = measuredAt;
    }

    @Override
    public ApplianceProcessResponse process(MutableEntry<UUID, ApplianceLiveState> entry, Object... args) {
        if (measuredWatt == null) {
            return null;
        }

        ApplianceLiveState state = entry.getValue();

        if (state == null) {
            state = new ApplianceLiveState();
            state.setApplianceId(entry.getKey());
            state.setHomeId(homeId);
            state.setConsecutiveBreachCount(0);
        } else if (isStale(state.getLastMeasuredAt())) {
            return null;
        }

        state.setLastMeasuredWatt(measuredWatt);
        state.setLastMeasuredAt(measuredAt);

        boolean anomalyTriggered = false;
        BigDecimal safeWattLimit = state.getSafeWattLimit();
        if (safeWattLimit != null) {
            if (measuredWatt.compareTo(safeWattLimit) > 0) {
                int breachCount = state.getConsecutiveBreachCount() + 1;
                state.setConsecutiveBreachCount(breachCount);
                if (breachCount >= BREACH_THRESHOLD && !state.isAnomalous()) {
                    state.setAnomalous(true);
                    anomalyTriggered = true;
                }
            } else {
                state.setConsecutiveBreachCount(0);
                state.setAnomalous(false);
            }
        }

        entry.setValue(state);
        return new ApplianceProcessResponse(
                state.getApplianceId(),
                state.getHomeId(),
                measuredWatt,
                safeWattLimit,
                state.getConsecutiveBreachCount(),
                anomalyTriggered);
    }

    /**
     * Gelen olcum, cache'teki son olcumden eski mi?
     */
    private boolean isStale(Instant lastMeasuredAt) {
        return lastMeasuredAt != null
                && measuredAt != null
                && measuredAt.isBefore(lastMeasuredAt);
    }
}