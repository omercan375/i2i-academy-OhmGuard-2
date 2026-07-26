package org.example.i2iacademyohmguard2.anomaly_events;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.ai_recommendations.RecommendationService;
import org.example.i2iacademyohmguard2.ai_recommendations.TriggerType;
import org.example.i2iacademyohmguard2.anomaly_events.dto.AnomalyEventDto;
import org.example.i2iacademyohmguard2.appliances.ApplianceRepo;
import org.example.i2iacademyohmguard2.appliances.AppliancesTable;
import org.example.i2iacademyohmguard2.homes.HomesService;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.ignite.appliance.dto.ApplianceProcessResponse;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnomalyEventsService {

    private final AnomalyEventsRepo anomalyEventsRepo;
    private final HomesService homesService;
    private final ApplianceRepo applianceRepo;
    private final RecommendationService recommendationService;

    @Transactional
    public void createAnomalyEvent(ApplianceProcessResponse response) {
        HomesTable home = homesService.findHome(response.homeId());
        AppliancesTable appliance = applianceRepo.findById(response.applianceId()).orElse(null);
        if (appliance == null) {
            return;
        }

        AnomalyEventsTable anomalyEvent = AnomalyEventsTable.builder()
                .home(home)
                .appliance(appliance)
                .measuredWatt(response.measuredWatt())
                .safeWattLimit(response.safeWattLimit())
                .breachCount(response.breachCount())
                .build();
        anomalyEventsRepo.save(anomalyEvent);

        recommendationService.generate(home, appliance, TriggerType.ANOMALY);
    }

    public List<AnomalyEventDto> anomalyHistory(UsersTable user, UUID homeId) {
        List<AnomalyEventDto> history = new ArrayList<>();

        List<AnomalyEventsTable> events = anomalyEventsRepo.anomalyHistory(homeId, user.getId());
        for (AnomalyEventsTable event : events) {
            AnomalyEventDto dto = AnomalyEventDto.builder()
                    .applianceId(event.getAppliance().getId())
                    .applianceName(event.getAppliance().getName())
                    .measuredWatt(event.getMeasuredWatt())
                    .safeWattLimit(event.getSafeWattLimit())
                    .breachCount(event.getBreachCount())
                    .detectedAt(event.getDetectedAt())
                    .build();
            history.add(dto);
        }
        return history;
    }
}
