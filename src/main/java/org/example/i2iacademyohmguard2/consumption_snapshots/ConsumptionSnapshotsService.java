package org.example.i2iacademyohmguard2.consumption_snapshots;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.consumption_snapshots.dto.DailyConsumptionSnapshotsHistoryDto;
import org.example.i2iacademyohmguard2.consumption_snapshots.dto.MonthlyConsumptionSnapshotsHistoryDto;
import org.example.i2iacademyohmguard2.homes.HomesRepo;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveState;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveStateService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumptionSnapshotsService { private final HomeLiveStateService homeLiveStateService;
    private final ConsumptionSnapshotsRepo consumptionSnapshotsRepo;
    private final HomesRepo homesRepo;


    /*her ay aylık tüketimleri db ye kayıt et*/
    public void createConsumptionSnapshot(HomesTable home){
        saveSnapshot(home, ConsumptionSnapshotsType.MONTHLY);
    }

    public void createDailySnapshotsForActiveHomes(){
        for (HomesTable home : homesRepo.findAllActive()) {
            saveSnapshot(home, ConsumptionSnapshotsType.DAILY);
        }
    }

    private void saveSnapshot(HomesTable home, ConsumptionSnapshotsType type){
        HomeLiveState homeIgnite = homeLiveStateService.getHomeLiveState(home.getId());
        if (homeIgnite == null) {
            return;
        }
        ConsumptionSnapshotsTable newConsumptionSnapshotsTable = ConsumptionSnapshotsTable.builder()
                .home(home)
                .energyKwh(homeIgnite.getAccumulatedKwh())
                .totalCost(homeIgnite.getAccumulatedCost())
                .type(type)
                .build();

        consumptionSnapshotsRepo.save(newConsumptionSnapshotsTable);
    }

    /*aylık history*/
    public List<MonthlyConsumptionSnapshotsHistoryDto> monthlyHistory(UsersTable user, UUID homeId){
        List<MonthlyConsumptionSnapshotsHistoryDto> history = new ArrayList<>();

        List<ConsumptionSnapshotsTable> getAllByHomeAndOwnerId = consumptionSnapshotsRepo.findTypeByHomeIdAndOwnerId(homeId,user.getId(),ConsumptionSnapshotsType.MONTHLY);

        for (ConsumptionSnapshotsTable home: getAllByHomeAndOwnerId) {
            MonthlyConsumptionSnapshotsHistoryDto newConsumptionSnapshots = MonthlyConsumptionSnapshotsHistoryDto.builder()
                    .energyKwh(home.getEnergyKwh())
                    .totalCost(home.getTotalCost())
                    .recordedAt(home.getRecordedAt())
                    .build();
            history.add(newConsumptionSnapshots);
        }
        return history;
    }

    /*günlük history (public)*/
    public List<DailyConsumptionSnapshotsHistoryDto> dailyHistory(UUID homeId){
        List<DailyConsumptionSnapshotsHistoryDto> history = new ArrayList<>();

        List<ConsumptionSnapshotsTable> getAllByHomeAndOwnerId = consumptionSnapshotsRepo.findByHomeIdAndType(homeId,ConsumptionSnapshotsType.DAILY);

        for (ConsumptionSnapshotsTable home: getAllByHomeAndOwnerId) {
            DailyConsumptionSnapshotsHistoryDto newConsumptionSnapshots = DailyConsumptionSnapshotsHistoryDto.builder()
                    .energyKwh(home.getEnergyKwh())
                    .totalCost(home.getTotalCost())
                    .recordedAt(home.getRecordedAt())
                    .build();
            history.add(newConsumptionSnapshots);
        }
        return history;
    }

}
