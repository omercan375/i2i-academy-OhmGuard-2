package org.example.i2iacademyohmguard2.appliances;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ApplianceRepo extends CrudRepository<AppliancesTable,UUID> {
    @Query("SELECT a FROM AppliancesTable a WHERE a.name=:appliancesName and a.home.id=:homeId")
    AppliancesTable getAppliancesByNameAndHomeId(@Param("appliancesName") String appliancesName,@Param("homeId") UUID homeId);

    @Query("SELECT a FROM AppliancesTable a WHERE a.home.id=:homeId")
    List<AppliancesTable> findAllByHomeId(@Param("homeId") UUID homeId);

    @Query("SELECT a FROM AppliancesTable a JOIN FETCH a.home h JOIN FETCH h.owner o WHERE h.id = :homeId AND o.id = :ownerId ORDER BY a.active DESC")
    List<AppliancesTable> getAppliancesByHomeIdAndOwnerId(@Param("homeId") UUID homeId, @Param("ownerId") UUID ownerId);

    @Query("SELECT a FROM AppliancesTable a JOIN FETCH a.home h JOIN FETCH h.owner o WHERE a.id=:applianceId AND h.id=:homeId AND o.id=:ownerId")
    AppliancesTable findApplianceByApplianceIdAndByHomeIdAndOwnerId(@Param("applianceId") UUID applianceId,@Param("homeId") UUID homeId, @Param("ownerId") UUID ownerId);

    @Modifying
    @Query("UPDATE AppliancesTable a SET a.safeWattLimit=:newSafeWattLimit,a.version=a.version+1 WHERE a.id=:applianceId and a.version=:version")
    int updateSafeWattLimit(@Param("newSafeWattLimit")BigDecimal newSafeWattLimit,@Param("applianceId") UUID applianceId,@Param("version") Integer version);

    @Modifying
    @Query("DELETE FROM AppliancesTable a WHERE a.id=:applianceId and a.version=:version")
    int deleteById(@Param("applianceId") UUID applianceId,@Param("version") Integer version);

}
