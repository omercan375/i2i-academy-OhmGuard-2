package org.example.i2iacademyohmguard2.anomaly_events;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.anomaly_events.dto.AnomalyEventDto;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/anomaly")
@RequiredArgsConstructor
public class AnomalyEventsController {

    private final UsersService usersService;
    private final AnomalyEventsService anomalyEventsService;

    @GetMapping("/history")
    public ResponseEntity<List<AnomalyEventDto>> anomalyHistory(@RequestHeader("Authorization") String token, @RequestParam @NotNull UUID homeId) {
        UsersTable user = usersService.findByToken(token);
        List<AnomalyEventDto> history = anomalyEventsService.anomalyHistory(user, homeId);
        return ResponseEntity.ok(history);
    }
}
