package org.example.i2iacademyohmguard2.quota_events;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuotaEventsRepo extends CrudRepository<QuotaEventsTable, UUID> {
    @Query("SELECT q FROM QuotaEventsTable q where q.home.id=:homeId")
    QuotaEventsTable findAlertByHomeId(@Param("homeId")UUID homeId);

    @Query("SELECT q FROM QuotaEventsTable q JOIN FETCH q.home h JOIN FETCH h.owner o WHERE h.id=:homeId AND o.id=:ownerId")
    List<QuotaEventsTable> quotaHistory(@Param("homeId") UUID homeId , @Param("ownerId") UUID ownerId);



}
