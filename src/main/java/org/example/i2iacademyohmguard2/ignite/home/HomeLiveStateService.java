package org.example.i2iacademyohmguard2.ignite.home;

import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;
import org.example.i2iacademyohmguard2.ignite.IgniteConfig;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveState;
import org.example.i2iacademyohmguard2.ignite.home.dto.ProcessResponse;
import org.example.i2iacademyohmguard2.kafka.TelemetryMessage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeLiveStateService {

    private final Ignite ignite;

    private IgniteCache<UUID, HomeLiveState> homeLiveStateCache() {
        return ignite.cache(IgniteConfig.HOME_LIVE_STATE_CACHE);
    }

    /**
     * Ev durumunu Ignite cache içerisinden getirir.
     */
    public HomeLiveState getHomeLiveState(UUID homeId) {
        return homeLiveStateCache().get(homeId);
    }

    public void removeHome(UUID homeId) {
        homeLiveStateCache().remove(homeId);
    }

    /**
     * Yeni bir evi başlangıç değerleriyle Ignite cache'e ekler.
     *
     * putIfAbsent kullanıldığı için ev zaten varsa birikmiş tüketim
     * değerlerinin yanlışlıkla sıfırlanması engellenir.
     */
    public void initHome(
            UUID homeId,
            BigDecimal normalRate,
            BigDecimal penaltyRate,
            BigDecimal budgetLimit
    ) {
        HomeLiveState initialState = new HomeLiveState();

        initialState.setHomeId(homeId);

        initialState.setNormalRate(normalRate);
        initialState.setPenaltyRate(penaltyRate);
        initialState.setBudgetLimit(budgetLimit);

        initialState.setAccumulatedKwh(BigDecimal.ZERO);
        initialState.setAccumulatedCost(BigDecimal.ZERO);

        initialState.setActiveTariff(ActiveTariff.NORMAL);

        initialState.setAlert80Sent(false);
        initialState.setAlert100Sent(false);

        initialState.setUpdatedAt(Instant.now());

        initialState.setQuotaVersion(0);

        boolean created = homeLiveStateCache()
                .putIfAbsent(homeId, initialState);

        if (!created) {
            throw new IllegalStateException(
                    "Home Ignite cache içerisinde zaten bulunuyor: " + homeId
            );
        }
    }

    /**
     * Kafka'dan gelen telemetriyi ilgili homeId üzerinden atomik olarak işler.
     *
     * HomeStateProcessor tarafından döndürülen kota sonuçlarını consumer'a
     * geri verir.
     */
    public ProcessResponse processTelemetry(TelemetryMessage message) {
        int intervalSeconds = (message.intervalSeconds() != null && message.intervalSeconds() > 0)
                ? message.intervalSeconds()
                : 1;
        return homeLiveStateCache().invoke(
                message.homeId(),
                new HomeStateProcessor(
                        message.measuredWatt(),
                        intervalSeconds
                )
        );
    }
    public void updateBudgetLimit(UUID homeId, BigDecimal budgetLimit) {
        homeLiveStateCache().invoke(homeId, (entry, args) -> {
            HomeLiveState state = entry.getValue();
            if (state != null) {
                state.setBudgetLimit(budgetLimit);

                BigDecimal accumulatedCost = state.getAccumulatedCost();
                BigDecimal percent80 = budgetLimit.multiply(BigDecimal.valueOf(80))
                        .divide(BigDecimal.valueOf(100), 12, java.math.RoundingMode.HALF_EVEN);

                if (budgetLimit.compareTo(accumulatedCost) <= 0) {
                    state.setAlert80Sent(true);
                    state.setAlert100Sent(true);
                    state.setActiveTariff(ActiveTariff.PENALTY);
                    state.setQuotaVersion(1);
                } else if (percent80.compareTo(accumulatedCost) <= 0) {
                    state.setAlert80Sent(true);
                    state.setAlert100Sent(false);
                    state.setActiveTariff(ActiveTariff.NORMAL);
                    state.setQuotaVersion(1);
                } else {
                    state.setAlert80Sent(false);
                    state.setAlert100Sent(false);
                    state.setActiveTariff(ActiveTariff.NORMAL);
                    state.setQuotaVersion(0);
                }
                state.setUpdatedAt(Instant.now());
                entry.setValue(state);
            }
            return null;
        });
    }
    public void updateTariffRate(UUID homeId,BigDecimal newNormalRate, BigDecimal newPenaltyRate) {
        homeLiveStateCache().invoke(homeId, (entry, args) -> {
            HomeLiveState state = entry.getValue();
            if (state != null) {
                state.setNormalRate(newNormalRate);
                state.setPenaltyRate(newPenaltyRate);
                entry.setValue(state);
            }
            return null;
        });
    }
    public void cleanBillingAccount(UUID homeId) {
        homeLiveStateCache().invoke(homeId, (entry, args) -> {
            HomeLiveState state = entry.getValue();
            if (state != null) {
                state.setAccumulatedKwh(BigDecimal.ZERO);
                state.setAccumulatedCost(BigDecimal.ZERO);
                state.setAlert80Sent(false);
                state.setAlert100Sent(false);
                state.setActiveTariff(ActiveTariff.NORMAL);
                state.setUpdatedAt(Instant.now());
                state.setQuotaVersion(0);
                entry.setValue(state);

            }
            return null;
        });
    }
}