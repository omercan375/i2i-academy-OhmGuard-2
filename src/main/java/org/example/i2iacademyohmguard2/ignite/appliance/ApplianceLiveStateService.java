package org.example.i2iacademyohmguard2.ignite.appliance;

import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.example.i2iacademyohmguard2.ignite.IgniteConfig;
import org.example.i2iacademyohmguard2.ignite.appliance.dto.ApplianceProcessResponse;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveState;
import org.example.i2iacademyohmguard2.kafka.TelemetryMessage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Ignite'taki canli cihaz durumunu yoneten servis (applianceLiveStateCache).
 * <p>
 * Yazma noktasi {@link #saveTelemetry(TelemetryMessage)}'dir: gelen son olcumu
 * {@code applianceLiveStateCache} icinde {@link LatestTelemetryProcessor} +
 * cache.invoke ile atomik olarak guncelleyip saklar.
 */
@Service
@RequiredArgsConstructor
public class ApplianceLiveStateService {

    private final Ignite ignite;

    private IgniteCache<UUID, ApplianceLiveState> applianceLiveStateCache() {
        return ignite.cache(IgniteConfig.APPLIANCE_LIVE_STATE_CACHE);
    }

    private IgniteCache<UUID, HomeLiveState> homeLiveStateCache() {
        return ignite.cache(IgniteConfig.HOME_LIVE_STATE_CACHE);
    }

    /**
     * Gelen telemetri mesajindaki son olcumu ilgili cihazin canli durumuna yazar.
     * <p>
     * Ayni applianceId'ye gelen es zamanli mesajlar icin atomiklik saglamak
     * uzere {@link LatestTelemetryProcessor} ile {@code cache.invoke} kullanilir.
     */
    public ApplianceProcessResponse saveTelemetry(TelemetryMessage message) {
        return applianceLiveStateCache().invoke(
                message.applianceId(),
                new LatestTelemetryProcessor(
                        message.homeId(),
                        message.measuredWatt(),
                        message.measuredAt()
                )
        );
    }

    /**
     * Bir cihazin mevcut canli durumunu dondurur (yoksa null). Okuma amaclidir.
     */
    public ApplianceLiveState getApplianceLiveState(UUID applianceId) {
        return applianceLiveStateCache().get(applianceId);
    }

    public List<ApplianceLiveState> getApplianceLiveStatesByHome(UUID homeId) {
        HomeLiveState home = homeLiveStateCache().get(homeId);
        if (home == null || home.getApplianceIds() == null || home.getApplianceIds().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(applianceLiveStateCache().getAll(home.getApplianceIds()).values());
    }

    public void updateNewSafeWattLimit(UUID applianceId, BigDecimal newSafeWattLimit) {
        applianceLiveStateCache().invoke(applianceId, (entry, args) -> {
            ApplianceLiveState state = entry.getValue();
            if (state != null) {
                state.setSafeWattLimit(newSafeWattLimit);
                entry.setValue(state);
            }
            return null;
        });
    }

    /**
     * Yeni kaydedilen cihaz icin canli durumu olusturur.
     * Idempotenttir: kayit varsa sifirdan yaratmaz, yalnizca limiti tazeler.
     */
    public void addAppliances(UUID applianceId, UUID homeId, String name, BigDecimal safeWattLimit) {
        applianceLiveStateCache().invoke(
                applianceId,
                new AddApplianceProcessor(applianceId, homeId, name, safeWattLimit));
        homeLiveStateCache().invoke(homeId, (entry, args) -> {
            HomeLiveState state = entry.getValue();
            if (state != null) {
                if (state.getApplianceIds() == null) {
                    state.setApplianceIds(new HashSet<>());
                }
                state.getApplianceIds().add(applianceId);
                entry.setValue(state);
            }
            return null;
        });
    }

    /**
     * Cihazin canli durumunu cache'ten siler. Silinen cihaza ait sayacin
     * bellekte kalip ileride sahte anomali uretmesini onler.
     */
    public boolean removeAppliances(UUID applianceId) {
        ApplianceLiveState state = applianceLiveStateCache().get(applianceId);
        if (state != null && state.getHomeId() != null) {
            homeLiveStateCache().invoke(state.getHomeId(), (entry, args) -> {
                HomeLiveState home = entry.getValue();
                if (home != null && home.getApplianceIds() != null) {
                    home.getApplianceIds().remove(applianceId);
                    entry.setValue(home);
                }
                return null;
            });
        }
        return applianceLiveStateCache().remove(applianceId);
    }
}
