package org.example.i2iacademyohmguard2.ai_recommendations.records;

import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Prompt uretiminde kullanilacak, canli state'ten toplanmis salt-veri paketi.
 *
 * Kota alarmlarinda appliance alanlari (applianceId, lastMeasuredWatt,
 * safeWattLimit, consecutiveBreachCount) null'dir; anomali tetikleyicisinde dolu olur.
 */
public record RecommendationContext(
        UUID homeId,
        String homeName,
        BigDecimal budgetLimit,
        BigDecimal accumulatedCost,
        BigDecimal accumulatedKwh,
        ActiveTariff activeTariff,
        BigDecimal usagePercent,
        UUID applianceId,
        BigDecimal lastMeasuredWatt,
        BigDecimal safeWattLimit,
        Integer consecutiveBreachCount,
        int priorRecommendationCount
) {
}
