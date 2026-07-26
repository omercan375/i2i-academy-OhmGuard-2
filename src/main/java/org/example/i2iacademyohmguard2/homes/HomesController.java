package org.example.i2iacademyohmguard2.homes;

import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.homes.dto.*;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomesController {
    private final UsersService usersService;
    private final HomesService homesService;

    @PostMapping("/create")
    public ResponseEntity<?> createHome(@RequestHeader("Authorization") String token, @Valid @RequestBody CreateHomeDto createHomeDto) {
        UsersTable findUserByToken = usersService.findByToken(token);
        homesService.createHome(findUserByToken,createHomeDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterHomeDto registerHomeDto) {
        homesService.registerPublic(registerHomeDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/create-full")
    public ResponseEntity<?> createFull(@RequestHeader("Authorization") String token, @Valid @RequestBody RegisterHomeDto registerHomeDto) {
        UsersTable user = usersService.findByToken(token);
        homesService.registerForUser(user, registerHomeDto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/mine/{homeId}")
    public ResponseEntity<?> deleteMine(@RequestHeader("Authorization") String token, @PathVariable UUID homeId) {
        UsersTable user = usersService.findByToken(token);
        homesService.deleteForUser(user, homeId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/dashboard/mine")
    public ResponseEntity<List<DashboardHomeDto>> dashboardMine(@RequestHeader("Authorization") String token) {
        UsersTable user = usersService.findByToken(token);
        return ResponseEntity.ok().body(homesService.dashboardForUser(user));
    }
    @PostMapping("/{homeId}/appliance")
    public ResponseEntity<?> addAppliance(@PathVariable UUID homeId, @Valid @RequestBody RegisterApplianceDto dto) {
        homesService.addAppliancePublic(homeId, dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{homeId}")
    public ResponseEntity<?> deleteHome(@PathVariable UUID homeId) {
        homesService.deletePublic(homeId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{homeId}/appliance/{applianceId}")
    public ResponseEntity<?> deleteAppliance(@PathVariable UUID homeId, @PathVariable UUID applianceId) {
        homesService.deleteAppliancePublic(homeId, applianceId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/show-all")
    public ResponseEntity<List<ShowAllHomeDto>> showAllHomes(@RequestHeader("Authorization") String token) {
        UsersTable findUserByToken = usersService.findByToken(token);
        List<ShowAllHomeDto> showAllHomes = homesService.showAllHomes(findUserByToken);
        return ResponseEntity.ok().body(showAllHomes);
    }
    @GetMapping("/info")
    public ResponseEntity<HomeInfo> getHomeInfo(@RequestHeader("Authorization") String token, @RequestParam @NotNull UUID homeId) {
        UsersTable findUserByToken = usersService.findByToken(token);
        HomeInfo homeInfo = homesService.getHomeInfo(findUserByToken,homeId);
        return ResponseEntity.ok().body(homeInfo);
    }
    @GetMapping("/status")
    public ResponseEntity<HomeStatusDto> getHomeStatus(@RequestParam @NotNull UUID homeId) {
        HomeStatusDto homeStatus = homesService.getHomeStatus(homeId);
        return ResponseEntity.ok().body(homeStatus);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<List<DashboardHomeDto>> dashboard() {
        return ResponseEntity.ok().body(homesService.dashboard());
    }
    @PutMapping("/update/budget-limit")
    public ResponseEntity<?> updateBudgetLimit(@RequestHeader("Authorization")String token, @Valid @RequestBody UpdateBudgetLimit updateBudgetLimitDto) {
        UsersTable findUserByToken = usersService.findByToken(token);
        homesService.updateBudgetLimit(findUserByToken,updateBudgetLimitDto);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/update/tariff-rates")
    public ResponseEntity<?> updateTariffRates(@RequestHeader("Authorization")String token, @Valid @RequestBody UpdateTariffLimits updateTariffLimits) {
        UsersTable findUserByToken = usersService.findByToken(token);
        homesService.updateTariffRates(findUserByToken,updateTariffLimits);
        return ResponseEntity.ok().build();
    }

}
