package org.example.i2iacademyohmguard2.anomaly_events;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnomalyEventsRepo extends CrudRepository<AnomalyEventsTable, UUID> {
    @Query("SELECT a FROM AnomalyEventsTable a JOIN FETCH a.home h JOIN FETCH h.owner o JOIN FETCH a.appliance ap WHERE h.id=:homeId AND o.id=:ownerId ORDER BY a.detectedAt DESC")
    List<AnomalyEventsTable> anomalyHistory(@Param("homeId") UUID homeId, @Param("ownerId") UUID ownerId);
}
