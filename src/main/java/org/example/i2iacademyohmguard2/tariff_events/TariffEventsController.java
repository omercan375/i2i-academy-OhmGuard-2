package org.example.i2iacademyohmguard2.tariff_events;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.tariff_events.dto.TariffEventsDto;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tariff_events")
@RequiredArgsConstructor
public class TariffEventsController {

    private final UsersService usersService;
    private final TariffEventsService tariffEventsService;

    @GetMapping("/history")
    public ResponseEntity<List<TariffEventsDto>> tariffEventsHistory(@RequestHeader("Authorization") String token, @RequestParam @NotNull UUID homeId) {
        UsersTable user = usersService.findByToken(token);
        List<TariffEventsDto> history = tariffEventsService.tariffEventsHistory(homeId, user.getId());
        return ResponseEntity.ok(history);
    }
}
