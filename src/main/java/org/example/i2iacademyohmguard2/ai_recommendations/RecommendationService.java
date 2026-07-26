package org.example.i2iacademyohmguard2.ai_recommendations;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.ai_recommendations.records.RecommendationContext;
import org.example.i2iacademyohmguard2.appliances.AppliancesTable;
import org.example.i2iacademyohmguard2.email_notifications.EmailNotificationsService;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveState;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveStateService;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveState;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * AI oneri uretiminin orkestratoru ve modulun disariya acik tek giris noktasi.
 *
 * Kafka consumer thread'ini bloklamamak icin {@code @Async} calisir. Akis:
 * canli home (ve anomalide appliance) state okunur -> context kurulur ->
 * prompt uretilir -> Gemini'den metin alinir -> PostgreSQL'e kaydedilir.
 *
 * Metot butunuyle try-catch ile sarilidir; hicbir hata cagirana (alarm akisi)
 * sizmaz. Bu servis Ignite EntryProcessor'lerinin DISINDA, normal Spring
 * katmaninda calisir (processor'ler remote node'da serilestirilir, bean tasiyamaz).
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final HomeLiveStateService homeLiveStateService;
    private final ApplianceLiveStateService applianceLiveStateService;
    private final PromptBuilder promptBuilder;
    private final GeminiClient geminiClient;
    private final AiRecommendationsRepo aiRecommendationsRepo;
    private final EmailNotificationsService emailNotificationsService;

    @Async("aiRecommendationExecutor")
    public void generate(HomesTable homee, AppliancesTable findAppliance, TriggerType type) {
        try {
            HomeLiveState home = homeLiveStateService.getHomeLiveState(homee.getId());
            if (home == null) {
                log.warn("Home live state bulunamadi; AI onerisi uretilmeyecek. homeId={}", homee.getId());
                return;
            }

            BigDecimal usagePercent = usagePercent(home.getAccumulatedCost(), home.getBudgetLimit());

            int priorRecommendationCount = aiRecommendationsRepo.findByHomeIdOrderByGeneratedAtDesc(homee.getId()).size();

            UUID applianceId = findAppliance != null ? findAppliance.getId() : null;

            BigDecimal lastMeasuredWatt = null;
            BigDecimal safeWattLimit = null;
            Integer consecutiveBreachCount = null;
            if (type == TriggerType.ANOMALY && applianceId != null) {
                ApplianceLiveState appliance = applianceLiveStateService.getApplianceLiveState(applianceId);
                if (appliance != null) {
                    lastMeasuredWatt = appliance.getLastMeasuredWatt();
                    safeWattLimit = appliance.getSafeWattLimit();
                    consecutiveBreachCount = appliance.getConsecutiveBreachCount();
                }
            }

            RecommendationContext context = new RecommendationContext(
                    home.getHomeId(),
                    homee.getHomeName(),
                    home.getBudgetLimit(),
                    home.getAccumulatedCost(),
                    home.getAccumulatedKwh(),
                    home.getActiveTariff(),
                    usagePercent,
                    applianceId,
                    lastMeasuredWatt,
                    safeWattLimit,
                    consecutiveBreachCount,
                    priorRecommendationCount
            );

            String prompt = promptBuilder.build(context, type);
            String recommendationText = geminiClient.ask(prompt, type);

            AiRecommendationsTable recommendation = new AiRecommendationsTable();
            recommendation.setHome(homee);
            recommendation.setAppliance(type == TriggerType.ANOMALY ? findAppliance : null);
            recommendation.setTriggerType(type);
            recommendation.setRecommendationText(recommendationText);

            AiRecommendationsTable save = aiRecommendationsRepo.save(recommendation);
            emailNotificationsService.newEmail(recommendation);
        } catch (Exception e) {
            log.warn("AI oneri uretimi basarisiz; alarm akisi etkilenmeyecek. homeId={}, type={}, hata={}",
                    homee.getId(), type, e.getMessage(), e);
        }

    }

    /**
     * usagePercent = accumulatedCost / budgetLimit * 100 (2 ondalik, HALF_UP).
     * Butce sifir/null ise sıfır doner (bolme hatasini onler).
     */
    private BigDecimal usagePercent(BigDecimal accumulatedCost, BigDecimal budgetLimit) {
        if (accumulatedCost == null || budgetLimit == null || budgetLimit.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return accumulatedCost
                .divide(budgetLimit, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
