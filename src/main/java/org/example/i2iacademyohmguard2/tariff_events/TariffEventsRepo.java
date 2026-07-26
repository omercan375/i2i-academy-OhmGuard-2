package org.example.i2iacademyohmguard2.tariff_events;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TariffEventsRepo extends CrudRepository<TariffEventsTable,UUID> {
     @Query("SELECT t FROM TariffEventsTable t WHERE t.home.id=:homeId and t.home.owner.id=:ownerId")
    List<TariffEventsTable> tariffEventsHistory(@Param("homeId") UUID homeId,@Param("ownerId") UUID ownerId);


}
