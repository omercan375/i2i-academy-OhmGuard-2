package org.example.i2iacademyohmguard2.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Ekosistemin Kafka topic tanimlari.
 *
 * Spring Boot, uygulama acilisinda {@link NewTopic} bean'lerini KafkaAdmin
 * araciligiyla broker'a bildirir; topic yoksa olusturulur, varsa dokunulmaz.
 * Boylece docker-compose ile ayaga kalkan ortamda elle topic yaratmaya gerek
 * kalmaz.
 *
 * Topic isimleri ortam degiskeninden okunur; kod icinde sabitlenmez.
 *
 * Partition secimi: her iki topic'te de mesaj anahtari homeId'dir. Ayni evin
 * mesajlari ayni partition'a dustugu icin ev bazinda sira garantisi korunur
 * (once HOME_CREATED, sonra APPLIANCE_ADDED gibi). Partition sayisi ayni
 * zamanda tuketici tarafindaki azami paralellik sinirini belirler.
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Yasam dongusu olaylari: yeni ev ve cihaz kayitlari, cihaz kaldirma.
     * Dusuk hacimli oldugu icin 3 partition yeterlidir.
     */
    @Bean
    public NewTopic registrationTopic(
            @Value("${voltwise.kafka.registration-topic}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Yuksek frekansli telemetri akisi. Cihaz sayisiyla dogru orantili yuk
     * tasidigi icin registration topic'ine gore daha fazla partition alir.
     */
    @Bean
    public NewTopic telemetryTopic(
            @Value("${voltwise.kafka.telemetry-topic}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(6)
                .replicas(1)
                .build();
    }
}