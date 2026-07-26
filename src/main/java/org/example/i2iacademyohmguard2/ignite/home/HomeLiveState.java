package org.example.i2iacademyohmguard2.ignite.home;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Bir evin Apache Ignite'ta tutulan canli durumu (key = homeId).
 *
 * SADECE veri tutar. Bu alanlar uzerinde hicbir hesap/karar burada yapilmaz;
 * degerleri disaridan (is mantigini yazan katman) verilir ve atomik olarak
 * yazilir.
 *
 * Ignite cache degeri ve EntryProcessor uzerinden tasindigi icin Serializable'dir.
 */
@Getter
@Setter
@NoArgsConstructor
public class HomeLiveState implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID homeId;

    private BigDecimal NormalRate;
    private BigDecimal penaltyRate;
    private BigDecimal budgetLimit;
    private BigDecimal accumulatedKwh;
    private BigDecimal accumulatedCost;
    private ActiveTariff activeTariff;
    private boolean alert80Sent;
    private boolean alert100Sent;
    private Instant updatedAt;
    private int quotaVersion;
    private Set<UUID> applianceIds = new HashSet<>();
}
