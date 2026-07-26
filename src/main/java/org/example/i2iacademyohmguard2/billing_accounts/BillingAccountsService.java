package org.example.i2iacademyohmguard2.billing_accounts;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.billing_accounts.dto.BillingAccountInfos;
import org.example.i2iacademyohmguard2.consumption_snapshots.ConsumptionSnapshotsService;
import org.example.i2iacademyohmguard2.homes.HomesRepo;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveState;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveStateService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.example.i2iacademyohmguard2.zcommon.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingAccountsService {

    private final BillingAccountsRepo billingAccountsRepo;
    private final HomeLiveStateService homeLiveStateService;
    private final HomesRepo homesRepo;
    private final ConsumptionSnapshotsService consumptionSnapshotsService;

    @Transactional
    public void syncBillingAccounts(){
        for(HomesTable home : homesRepo.findAllActive()){
            HomeLiveState state = homeLiveStateService.getHomeLiveState(home.getId());
            if(state == null){
                continue;
            }
            billingAccountsRepo.syncBalance(state.getAccumulatedKwh(), state.getAccumulatedCost(), state.getActiveTariff(), home.getId());
        }
    }
    public BillingAccountInfos getBillingAccountInfos(UsersTable usersTable,  UUID homeId){
        BillingAccountsTable newBillingAccount = billingAccountsRepo.findByHomeIdAndOwnerId(homeId, usersTable.getId());
        if(newBillingAccount == null){
            throw new ResourceNotFoundException("Billing account not found");
        }
        return BillingAccountInfos.builder()
                .accumulatedEnergyKwh(newBillingAccount.getAccumulatedEnergyKwh())
                .accumulatedCost(newBillingAccount.getAccumulatedCost())
                .activeTariff(newBillingAccount.getActiveTariff())
                .build();
    }

    @Transactional
    public void cleanBillingAccounts(){
        List<UUID> updateAll = new ArrayList<>();
        List<HomesTable> findActive = homesRepo.findAllActive();
        for(HomesTable home : findActive){
            /*önce consumption snapshotını oluşturulaım*/
            consumptionSnapshotsService.createConsumptionSnapshot(home);
            updateAll.add(home.getId());
        }
        billingAccountsRepo.cleanBillingAccount(updateAll);

        /*clean edildikten sonra home ignite sıfırlanmalı */
        for(UUID homeId : updateAll){
            homeLiveStateService.cleanBillingAccount(homeId);
        }


    }
}
