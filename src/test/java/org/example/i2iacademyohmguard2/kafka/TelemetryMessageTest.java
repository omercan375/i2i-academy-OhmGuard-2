package org.example.i2iacademyohmguard2.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TelemetryMessage DTO'sunun Kafka'dan gelen JSON'dan dogru deserialize
 * oldugunu ve temel dogrulamalarin calistigini test eder.
 */
class TelemetryMessageTest {

    private JsonDeserializer<TelemetryMessage> deserializer() {
        JsonDeserializer<TelemetryMessage> deserializer =
                new JsonDeserializer<>(TelemetryMessage.class);
        deserializer.addTrustedPackages("org.example.i2iacademyohmguard2.kafka");
        return deserializer;
    }

    @Test
    void kafkaJsonMesaji_dogru_deserialize_olur() {
        String json = """
                {
                  "homeId": "5dd920f1-27fe-438a-86dd-8fa6c6817d7e",
                  "applianceId": "ef92e147-8487-4d08-8557-eac125847abd",
                  "measuredWatt": 2200.50,
                  "measuredAt": "2026-07-21T13:30:00Z"
                }
                """;

        TelemetryMessage message = deserializer()
                .deserialize("ohmguard.telemetry", json.getBytes(StandardCharsets.UTF_8));

        assertThat(message.homeId())
                .isEqualTo(UUID.fromString("5dd920f1-27fe-438a-86dd-8fa6c6817d7e"));
        assertThat(message.applianceId())
                .isEqualTo(UUID.fromString("ef92e147-8487-4d08-8557-eac125847abd"));
        assertThat(message.measuredWatt()).isEqualByComparingTo("2200.50");
        assertThat(message.measuredAt()).isEqualTo(Instant.parse("2026-07-21T13:30:00Z"));
    }

    @Test
    void opsiyonel_intervalSeconds_alani_deserialize_edilir() {
        String json = """
                {
                  "homeId": "5dd920f1-27fe-438a-86dd-8fa6c6817d7e",
                  "applianceId": "ef92e147-8487-4d08-8557-eac125847abd",
                  "measuredWatt": 100,
                  "measuredAt": "2026-07-21T13:30:00Z",
                  "intervalSeconds": 5
                }
                """;

        TelemetryMessage message = deserializer()
                .deserialize("ohmguard.telemetry", json.getBytes(StandardCharsets.UTF_8));

        assertThat(message.intervalSeconds()).isEqualTo(5);
    }

    @Test
    void negatif_measuredWatt_reddedilir() {
        assertThatThrownBy(() -> new TelemetryMessage(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("-1"),
                Instant.now(),
                null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negatif");
    }

    @Test
    void zorunlu_alanlar_null_ise_reddedilir() {
        Instant now = Instant.now();
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> new TelemetryMessage(null, id, BigDecimal.ONE, now, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TelemetryMessage(id, null, BigDecimal.ONE, now, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TelemetryMessage(id, id, null, now, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TelemetryMessage(id, id, BigDecimal.ONE, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
