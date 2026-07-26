package org.example.i2iacademyohmguard2.ignite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.i2iacademyohmguard2.appliances.ApplianceRepo;
import org.example.i2iacademyohmguard2.appliances.AppliancesTable;
import org.example.i2iacademyohmguard2.homes.HomesRepo;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveStateService;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveStateService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IgniteRehydrator {

    private final HomesRepo homesRepo;
    private final ApplianceRepo applianceRepo;
    private final HomeLiveStateService homeLiveStateService;
    private final ApplianceLiveStateService applianceLiveStateService;

    @EventListener(ApplicationReadyEvent.class)
    public void rehydrate() {
        int homes = 0, appliances = 0;
        for (HomesTable home : homesRepo.findAll()) {
            try {
                homeLiveStateService.initHome(home.getId(), home.getNormalTariffRate(), home.getPenaltyTariffRate(), home.getBudgetLimit());
                homes++;
            } catch (Exception e) {
                log.debug("Home zaten Ignite'ta: {}", home.getId());
            }
            for (AppliancesTable appliance : applianceRepo.findAllByHomeId(home.getId())) {
                applianceLiveStateService.addAppliances(appliance.getId(), home.getId(), appliance.getName(), appliance.getSafeWattLimit());
                appliances++;
            }
        }
        log.info("Ignite rehydrate tamamlandi: {} ev, {} cihaz yuklendi.", homes, appliances);
    }
}
