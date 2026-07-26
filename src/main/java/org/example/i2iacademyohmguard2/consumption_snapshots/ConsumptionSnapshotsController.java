package org.example.i2iacademyohmguard2.consumption_snapshots;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.consumption_snapshots.dto.DailyConsumptionSnapshotsHistoryDto;
import org.example.i2iacademyohmguard2.consumption_snapshots.dto.MonthlyConsumptionSnapshotsHistoryDto;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/consumption-service")
@RequiredArgsConstructor
public class ConsumptionSnapshotsController {
    private final ConsumptionSnapshotsService consumptionSnapshotsService;
    private final UsersService usersService;




    @Scheduled(cron = "0 0 0 * * ?")
    public void recordDailySnapshots(){
        consumptionSnapshotsService.createDailySnapshotsForActiveHomes();
    }

    @GetMapping("/monthly-history")
    public ResponseEntity<List<MonthlyConsumptionSnapshotsHistoryDto>> history(@RequestHeader("Authorization") String token, @RequestParam @NotNull UUID homeId){
        UsersTable user = usersService.findByToken(token);
        List<MonthlyConsumptionSnapshotsHistoryDto> history = consumptionSnapshotsService.monthlyHistory(user, homeId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/daily-history")
    public ResponseEntity<List<DailyConsumptionSnapshotsHistoryDto>> dailyHistory(@RequestParam @NotNull UUID homeId){
        List<DailyConsumptionSnapshotsHistoryDto> history = consumptionSnapshotsService.dailyHistory(homeId);
        return ResponseEntity.ok(history);
    }
}
