package org.example.i2iacademyohmguard2.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.i2iacademyohmguard2.anomaly_events.AnomalyEventsService;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveStateService;
import org.example.i2iacademyohmguard2.ignite.appliance.dto.ApplianceProcessResponse;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveStateService;
import org.example.i2iacademyohmguard2.ignite.home.dto.ProcessResponse;
import org.example.i2iacademyohmguard2.quota_events.QuotaEventsService;
import org.example.i2iacademyohmguard2.tariff_events.TariffEventsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Sensor telemetri mesajlarini dinleyen tek Kafka listener'i.
 *
 * Sorumlulugu SADECE mesaji almak ve son canli olcumu Ignite'a yazmasi icin
 * {@link ApplianceLiveStateService}'e devretmektir. Burada hicbir is mantigi
 * (kota, tarife, anomali, billing) calistirilmaz ve exception yutulmaz.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryConsumer {

    private final ApplianceLiveStateService applianceLiveStateService;
    private final HomeLiveStateService homeLiveStateService;
    private final QuotaEventsService quotaEventsService;
    private final TariffEventsService tariffEventsService;
    private final AnomalyEventsService anomalyEventsService;

    @KafkaListener(
            topics = "${app.kafka.telemetry-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )

    // UUID homeId, BigDecimal newAccumulatedKwh, BigDecimal newAccumulatedCost, Boolean alert80, boolean alert100,boolean alert100Previous


    public void consume(TelemetryMessage message) {
        log.debug("Telemetri mesaji alindi: applianceId={}, homeId={}, measuredWatt={}, measuredAt={}",
                message.applianceId(), message.homeId(), message.measuredWatt(), message.measuredAt());
        ApplianceProcessResponse applianceResponse = applianceLiveStateService.saveTelemetry(message);
        /*homeLiveStateService den record respond da
        * yeni toplam kwh kullanımı (1 aylık)
        * yeni toplam elektrik kullanım bedeli (1 aylık)
        * 80 kotasının aşılıp aşılmadığı
        * 100 kotasının aşılıp aşılmadığı
        * dönüyor
        * */

        ProcessResponse response = homeLiveStateService.processTelemetry(message);
        if (response == null) {
            return;
        }
        /*kota aşımlarını tespit ediyor*/
        try {
            quotaEventsService.alertQuota(response);
        } catch (Exception e) {
            log.error("Kota loglama basarisiz. homeId={}", response.homeId(), e);
        }
        /*eğer tarife değiştiyse (normal -> penalty veya penalty -> normal) onları tespit ediyor*/
        try {
            tariffEventsService.createTariffEvents(response.homeId(), response.alert100(), response.alert100Previous());
        } catch (Exception e) {
            log.error("Tarife loglama basarisiz. homeId={}", response.homeId(), e);
        }
        if (applianceResponse != null && applianceResponse.anomalyTriggered()) {
            try {
                anomalyEventsService.createAnomalyEvent(applianceResponse);
            } catch (Exception e) {
                log.error("Anomali kaydi olusturulamadi. applianceId={}, homeId={}",
                        applianceResponse.applianceId(), applianceResponse.homeId(), e);
            }
        }
    }
}
