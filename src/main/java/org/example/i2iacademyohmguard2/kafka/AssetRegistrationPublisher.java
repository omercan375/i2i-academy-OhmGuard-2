package org.example.i2iacademyohmguard2.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.i2iacademyohmguard2.kafka.registration.AssetRegistrationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Asset registration olaylarini Kafka'ya gonderen tek nokta.
 *
 * Servis katmani Kafka'yi hic gormez; yalnizca Spring'in
 * {@code ApplicationEventPublisher}'i ile bir {@link AssetRegistrationEvent}
 * yayinlar. Bu sinif o olayi VERITABANI COMMIT'INDEN SONRA yakalayip broker'a
 * iletir.
 *
 * Neden AFTER_COMMIT:
 * Kafka gonderimi JPA transaction'ina dahil degildir. Mesaj commit'ten once
 * gonderilirse ve transaction sonradan rollback olursa, simulator veritabaninda
 * var olmayan bir cihaz icin telemetri uretmeye baslar; silme akisinda ise
 * tersi olur (kayit durur ama simulasyon durur). AFTER_COMMIT ile olay yalnizca
 * kalici hale gelmis degisiklikler icin yayilir.
 *
 * Neden key = homeId:
 * Ayni anahtara sahip mesajlar ayni partition'a dustugu icin bir evin
 * olaylari uretildikleri sirayla tuketilir (once HOME_CREATED, sonra
 * APPLIANCE_ADDED). Anahtar verilmezse mesajlar partition'lara dagilir ve
 * sira garantisi kaybolur.
 *
 * Hata politikasi: gonderim basarisiz olursa exception firlatilmaz, ERROR
 * loglanir. Transaction zaten commit edilmistir; burada firlatilan bir hata
 * onu geri alamaz, yalnizca cagiran akisi bozar.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AssetRegistrationPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${voltwise.kafka.registration-topic}")
    private String registrationTopic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(AssetRegistrationEvent event) {
        String key = event.homeId().toString();

        kafkaTemplate.send(registrationTopic, key, event)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Registration event gonderilemedi. type={}, homeId={}, applianceId={}",
                                event.eventType(), event.homeId(), event.applianceId(), throwable);
                    } else {
                        log.debug("Registration event gonderildi. type={}, homeId={}, partition={}, offset={}",
                                event.eventType(), event.homeId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}