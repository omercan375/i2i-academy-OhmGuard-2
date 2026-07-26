package org.example.i2iacademyohmguard2.homes;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "homes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomesTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UsersTable owner;
    @Column(name = "name")
    private String homeName;
    @Column(name = "contact_email")
    private String contactEmail;
    @Column(name = "budget_limit")
    private BigDecimal budgetLimit;
    @Column(name = "normal_tariff_rate")
    private BigDecimal normalTariffRate;
    @Column(name = "penalty_tariff_rate")
    private BigDecimal penaltyTariffRate;
    @Column(name = "is_active")
    private Boolean isActive;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    

}
