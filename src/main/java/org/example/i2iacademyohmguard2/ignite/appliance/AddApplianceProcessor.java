package org.example.i2iacademyohmguard2.ignite.appliance;


import org.apache.ignite.cache.CacheEntryProcessor;

import javax.cache.processor.MutableEntry;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Yeni kaydedilen bir cihaz icin canli durumu olusturan EntryProcessor.
 * <p>
 * Idempotenttir: kayit zaten varsa (event tekrar islendiginde veya telemetri
 * kayittan once gelip minimum state yarattiginda) state sifirdan yaratilmaz,
 * yalnizca guvenli limit yazilir. Boylece birikmis ardisik ihlal sayaci ve
 * son olcum bilgisi kaybolmaz.
 */
public record AddApplianceProcessor(UUID applianceId, UUID homeId, String name, BigDecimal safeWattLimit)
        implements CacheEntryProcessor<UUID, ApplianceLiveState, Void> {

    private static final long serialVersionUID = 1L;

    @Override
    public Void process(MutableEntry<UUID, ApplianceLiveState> entry, Object... args) {
        ApplianceLiveState state = entry.getValue();

        if (state == null) {
            state = new ApplianceLiveState();
            state.setApplianceId(applianceId);
            state.setHomeId(homeId);
            state.setConsecutiveBreachCount(0);
        }

        state.setName(name);
        state.setSafeWattLimit(safeWattLimit);

        entry.setValue(state);
        return null;
    }
}
