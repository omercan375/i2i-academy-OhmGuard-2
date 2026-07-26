package org.example.i2iacademyohmguard2.appliances;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appliances")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppliancesTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private HomesTable home;
    @Column(name = "name")
    private String name;
    @Column(name = "safe_watt_limit")
    private BigDecimal safeWattLimit;
    @Column(name = "active")
    private boolean active;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Version
    private int version;
}
