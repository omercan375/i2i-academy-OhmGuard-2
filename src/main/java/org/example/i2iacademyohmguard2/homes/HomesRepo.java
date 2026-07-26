package org.example.i2iacademyohmguard2.homes;


import io.swagger.v3.oas.annotations.Operation;
import org.example.i2iacademyohmguard2.homes.dto.HomeInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface HomesRepo extends CrudRepository<HomesTable,UUID> {
    @Query("SELECT h FROM HomesTable h WHERE h.owner.id=:ownerId and h.homeName=:homeName")
    HomesTable findByOwnerIdAndHomeName(@Param("ownerId") UUID  ownerId, @Param("homeName") String homeName);

    @Query("SELECT h FROM HomesTable h WHERE h.id=:homeId")
    HomesTable findByHomeId(@Param("homeId") UUID homeId);

    @Query("SELECT h FROM HomesTable h WHERE h.owner.id=:ownerId ORDER BY h.isActive DESC")
    List<HomesTable> findAllByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT h FROM HomesTable h WHERE h.owner.id=:ownerId and h.id=:homeId")
    HomesTable findHomeByOwnerIdAndHomeId(@Param("ownerId") UUID ownerId, @Param("homeId") UUID id);

    /*find active homes*/
    @Query("SELECT h FROM HomesTable h WHERE h.isActive=true" )
    List<HomesTable> findAllActive();

    @Modifying
    @Query("UPDATE HomesTable h SET h.budgetLimit=:newBudgetLimit ,h.version=h.version+1 WHERE h.id=:homeId and h.version=:version")
    int updateBudgetLimit (@Param("newBudgetLimit") BigDecimal newBudgetLimit, @Param("homeId") UUID homeId, @Param("version") Integer version);

    @Modifying
    @Query("UPDATE HomesTable h SET h.normalTariffRate=:newNormalTariff,h.penaltyTariffRate=:penaltyTariffRate,h.version=h.version+1 where h.id=:homeId and h.version=:version")
    int updateTariffRates(@Param("newNormalTariff") BigDecimal newNormalTariff,@Param("penaltyTariffRate") BigDecimal penaltyTariffRate, @Param("homeId") UUID homeId, @Param("version") Integer version);
}
