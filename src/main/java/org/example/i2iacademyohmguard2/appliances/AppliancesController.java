package org.example.i2iacademyohmguard2.appliances;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.appliances.dto.AddAppliancesDto;
import org.example.i2iacademyohmguard2.appliances.dto.ApplianceInfo;
import org.example.i2iacademyohmguard2.appliances.dto.ShowAllAppliancesDto;
import org.example.i2iacademyohmguard2.appliances.dto.UpdateSafeWattLimitDto;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appliances")
@RequiredArgsConstructor
@Validated
public class AppliancesController {

    private final AppliancesService appliancesService;
    private final UsersService usersService;

    @PostMapping("/add")
    public ResponseEntity<?> addAppliances(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody AddAppliancesDto addAppliancesDto) {
        UsersTable user = usersService.findByToken(token);
        appliancesService.addAppliances(user, addAppliancesDto);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/show-all")
    public ResponseEntity<List<ShowAllAppliancesDto>> showAllAppliances(@RequestHeader("Authorization") String token, @RequestParam @NotNull UUID homeId) {
        UsersTable user = usersService.findByToken(token);
        List<ShowAllAppliancesDto> showAllAppliances = appliancesService.showAllAppliance(user, homeId);
        return ResponseEntity.ok().body(showAllAppliances);
    }
    @GetMapping("/info")
    public ResponseEntity<ApplianceInfo> applianceInfo(@RequestHeader("Authorization") String token,
                                                       @RequestParam @NotNull UUID homeId,
                                                       @RequestParam @NotNull UUID applianceId) {
        UsersTable user = usersService.findByToken(token);
        ApplianceInfo applianceInfo = appliancesService.applianceInfo(user, homeId, applianceId);
        return ResponseEntity.ok().body(applianceInfo);
    }
    @PutMapping("/update/safe-watt-limit")
    public ResponseEntity<?> updateSafeWattLimit(@RequestHeader("Authorization")String token, @Valid @RequestBody UpdateSafeWattLimitDto updateSafeWattLimitDto) {
        UsersTable user = usersService.findByToken(token);
        appliancesService.updateSafeWattLimit(user, updateSafeWattLimitDto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAppliances(@RequestHeader("Authorization")String token, @RequestParam @NotNull UUID homeId,@RequestParam @NotNull UUID applianceId) {
        UsersTable user = usersService.findByToken(token);
        appliancesService.deleteAppliance(user.getId(), homeId, applianceId);
        return ResponseEntity.ok().build();
    }





}
