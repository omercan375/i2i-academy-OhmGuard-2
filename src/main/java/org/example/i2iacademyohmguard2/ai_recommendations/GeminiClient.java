package org.example.i2iacademyohmguard2.ai_recommendations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Gemini LLM cagrisindan SORUMLU tek sinif; baska bir sorumlulugu yoktur.
 *
 * API anahtari asla hardcode edilmez; ortam degiskeninden ({@code gemini.api-key})
 * okunur. Timeout / rate-limit / baglanti hatasi durumunda EXCEPTION FIRLATMAZ:
 * tetikleyici turune uygun hazir bir Turkce fallback metni doner ve WARN loglar.
 * Boylece Gemini erisilemese bile alarm akisi bozulmaz.
 */
@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model,
            @Value("${gemini.connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${gemini.read-timeout-ms:20000}") int readTimeoutMs) {
        this.apiKey = apiKey;
        this.model = model;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Verilen prompt'u Gemini'ye gonderir ve uretilen metni doner. Herhangi bir
     * hata durumunda {@code type}'a uygun Turkce fallback metni doner (asla
     * exception firlatmaz).
     */
    public String ask(String prompt, TriggerType type) {
        try {
            GeminiRequest requestBody = new GeminiRequest(
                    List.of(new GeminiContent(List.of(new GeminiPart(prompt)))));

            GeminiResponse response = restClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiResponse.class);

            String text = extractText(response);
            if (text == null || text.isBlank()) {
                log.warn("Gemini bos/gecersiz yanit dondu; fallback metin kullanilacak. type={}", type);
                return fallback(type);
            }
            return text.trim();
        } catch (Exception e) {
            log.warn("Gemini cagrisi basarisiz; fallback metin kullanilacak. type={}, hata={}", type, e.getMessage());
            return fallback(type);
        }
    }

    private String extractText(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return null;
        }
        GeminiContent content = response.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return null;
        }
        return content.parts().get(0).text();
    }

    private String fallback(TriggerType type) {
        return switch (type) {
            case QUOTA_80 -> "Enerji bütçenizin büyük bölümüne ulaştınız. "
                    + "Yüksek güç çeken cihazları (klima, ısıtıcı, çamaşır/bulaşık makinesi) daha kısa süre kullanın. "
                    + "Kullanılmayan cihazları prizde bırakmayın. "
                    + "Aydınlatmada gündüz doğal ışığı tercih edin.";
            case QUOTA_100 -> "Bütçe limitiniz aşıldı ve cezalı tarifeye geçildi. "
                    + "Zorunlu olmayan cihazları kapatın. "
                    + "Yüksek tüketimli işleri (ütü, ısıtma) mümkünse erteleyin. "
                    + "Klima/ısıtma ayarını bir kademe düşürerek maliyeti azaltın.";
            case ANOMALY -> "Bir cihazınız güvenli tüketim sınırını sürekli aşıyor. "
                    + "Cihazı kontrol edin ve gerekmiyorsa kapatın. "
                    + "Arıza belirtisi (aşırı ısınma, ses) varsa fişini çekip servise başvurun. "
                    + "Sorun geçene kadar bu cihazı kullanmaktan kaçının.";
        };
    }

    private record GeminiRequest(List<GeminiContent> contents) {
    }

    private record GeminiContent(List<GeminiPart> parts) {
    }

    private record GeminiPart(String text) {
    }

    private record GeminiResponse(List<GeminiCandidate> candidates) {
    }

    private record GeminiCandidate(GeminiContent content) {
    }
}
