package org.example.i2iacademyohmguard2.quota_events;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.quota_events.dto.QuotaHistoryDto;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quota")
@RequiredArgsConstructor
public class QuotaEventsController {

    private final UsersService usersService;
    private final QuotaEventsService quotaEventsService;


    @GetMapping("/history")
    public ResponseEntity<List<QuotaHistoryDto>> quotaHistory(@RequestHeader("Authorization") String token, @RequestParam @NotNull UUID homeId){
        UsersTable findId = usersService.findByToken(token);
        List<QuotaHistoryDto> quotaHistory = quotaEventsService.quotaHistory(findId, homeId);
        return ResponseEntity.ok(quotaHistory);
    }
}
