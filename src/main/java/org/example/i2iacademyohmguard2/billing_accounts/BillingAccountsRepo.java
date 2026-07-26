package org.example.i2iacademyohmguard2.billing_accounts;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BillingAccountsRepo extends CrudRepository<BillingAccountsTable, UUID> {
    @Query("SELECT b FROM BillingAccountsTable b WHERE b.homeTable.id=:homeId and b.homeTable.owner.id=:ownerId")
    BillingAccountsTable findByHomeIdAndOwnerId(@Param("homeId") UUID homeId, @Param("ownerId")  UUID ownerId);

    @Modifying
    @Query("UPDATE BillingAccountsTable b SET b.accumulatedEnergyKwh=0,b.accumulatedCost=0,b.activeTariff=org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff.NORMAL where b.homeTable.id in (:homeId)")
    int cleanBillingAccount(@Param("homeId") List<UUID> homeId);

    @Modifying
    @Query("UPDATE BillingAccountsTable b SET b.accumulatedEnergyKwh=:accumulatedEnergyKwh,b.accumulatedCost=:accumulatedCost,b.activeTariff=:activeTariff where b.homeTable.id=:homeId")
    int syncBalance(@Param("accumulatedEnergyKwh") BigDecimal accumulatedEnergyKwh, @Param("accumulatedCost") BigDecimal accumulatedCost, @Param("activeTariff") ActiveTariff activeTariff, @Param("homeId") UUID homeId);


}
