package org.example.i2iacademyohmguard2.billing_accounts;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "billing_accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillingAccountsTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "home_id")
    private HomesTable homeTable;
    @Column(name = "accumulated_energy_kwh")
    private BigDecimal accumulatedEnergyKwh;
    @Column(name = "accumulated_cost")
    private BigDecimal accumulatedCost;
    @Enumerated(EnumType.STRING)
    @Column(name = "active_tariff")
    private ActiveTariff activeTariff;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
