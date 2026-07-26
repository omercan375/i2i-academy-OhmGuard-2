package org.example.i2iacademyohmguard2.tariff_events;

import jakarta.persistence.*;
import lombok.*;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tariff_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffEventsTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private HomesTable home;
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_tariff")
    private TariffTypes previousTariff;
    @Enumerated(EnumType.STRING)
    @Column(name = "new_tariff")
    private TariffTypes newTariff;
    @CreationTimestamp
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;
}
