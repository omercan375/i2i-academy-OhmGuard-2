package org.example.i2iacademyohmguard2.quota_events;

import lombok.RequiredArgsConstructor;

import org.example.i2iacademyohmguard2.ai_recommendations.RecommendationService;
import org.example.i2iacademyohmguard2.ai_recommendations.TriggerType;
import org.example.i2iacademyohmguard2.homes.HomesService;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.ignite.home.dto.ProcessResponse;
import org.example.i2iacademyohmguard2.quota_events.dto.QuotaHistoryDto;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class QuotaEventsService {
    private final int threshold80 =  80;
    private final int threshold100 = 100;
    private final HomesService homesService;


    private final QuotaEventsRepo quotaEventsRepo;
    private final RecommendationService recommendationService;

    @Transactional
    public void alertQuota(ProcessResponse processResponse) {

        /*öncesinde boolean lar false ise detect et*/
        boolean crossed80 = processResponse.alert80() && !processResponse.alert80Previous();
        boolean crossed100 = processResponse.alert100() && !processResponse.alert100Previous();

        /*false ise döngüye hiç sokmadan return*/
        if(!crossed80 && !crossed100) {
            return;
        }

        /*find home*/
        HomesTable findHome = homesService.findHome(processResponse.homeId());
        if(crossed80){
            /*alert 80 uyarısı*/
            QuotaEventsTable quota80 = QuotaEventsTable.builder()
                    .home(findHome)
                    .thresholdPercentage(threshold80)
                    .budgetLimit(findHome.getBudgetLimit())
                    .accumulatedCost(processResponse.newAccumulatedCost())
                    .build();
            quotaEventsRepo.save(quota80);
            // email gönder
            recommendationService.generate(findHome,null, TriggerType.QUOTA_80);

        }
        if(crossed100){
            QuotaEventsTable quota100 = QuotaEventsTable.builder()
                    .home(findHome)
                    .thresholdPercentage(threshold100)
                    .budgetLimit(findHome.getBudgetLimit())
                    .accumulatedCost(processResponse.newAccumulatedCost())
                    .build();
            quotaEventsRepo.save(quota100);
            // email gönder
            recommendationService.generate(findHome,null, TriggerType.QUOTA_100);

        }

    }
    public List<QuotaHistoryDto> quotaHistory(UsersTable user, UUID homeId) {
        List<QuotaHistoryDto> quotaHistory = new ArrayList<>();

        List<QuotaEventsTable> quotas = quotaEventsRepo.quotaHistory(homeId,user.getId());
        for (QuotaEventsTable quota : quotas) {
            QuotaHistoryDto newQuota = QuotaHistoryDto.builder()
                    .thresholdPercentage(quota.getThresholdPercentage())
                    .budgetLimit(quota.getBudgetLimit())
                    .accumulatedCost(quota.getAccumulatedCost())
                    .createdAt(quota.getOccurredAt())
                    .build();
            quotaHistory.add(newQuota);
        }
        return quotaHistory;
    }


}
