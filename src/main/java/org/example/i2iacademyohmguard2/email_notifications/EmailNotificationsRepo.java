package org.example.i2iacademyohmguard2.email_notifications;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmailNotificationsRepo extends CrudRepository<EmailNotificationsTable,UUID> {
    @Query("SELECT e FROM EmailNotificationsTable e JOIN FETCH e.recommendations r JOIN FETCH r.home h ORDER BY e.createdAt DESC")
    List<EmailNotificationsTable> findRecent(Pageable pageable);

    @Query("SELECT e FROM EmailNotificationsTable e JOIN FETCH e.recommendations r JOIN FETCH r.home h WHERE h.owner.id=:ownerId ORDER BY e.createdAt DESC")
    List<EmailNotificationsTable> findRecentByOwner(@Param("ownerId") UUID ownerId, Pageable pageable);
}
