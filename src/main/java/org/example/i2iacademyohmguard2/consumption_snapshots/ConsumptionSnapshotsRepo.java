package org.example.i2iacademyohmguard2.consumption_snapshots;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ConsumptionSnapshotsRepo extends CrudRepository <ConsumptionSnapshotsTable,UUID>{
    @Query("SELECT c FROM ConsumptionSnapshotsTable c JOIN FETCH c.home h JOIN FETCH h.owner o WHERE h.id=:homeId AND o.id=:ownerId AND c.type=:type ORDER BY c.recordedAt ASC")
    List<ConsumptionSnapshotsTable> findTypeByHomeIdAndOwnerId(@Param("homeId") UUID homeId, @Param("ownerId") UUID ownerId,@Param("type") ConsumptionSnapshotsType type);

    @Query("SELECT c FROM ConsumptionSnapshotsTable c WHERE c.home.id=:homeId AND c.type=:type ORDER BY c.recordedAt ASC")
    List<ConsumptionSnapshotsTable> findByHomeIdAndType(@Param("homeId") UUID homeId, @Param("type") ConsumptionSnapshotsType type);

}
