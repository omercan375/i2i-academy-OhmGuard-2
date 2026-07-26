package org.example.i2iacademyohmguard2.ai_recommendations;

import org.example.i2iacademyohmguard2.ai_recommendations.records.RecommendationContext;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Tetikleyici turune gore Gemini'ye gonderilecek Turkce prompt metnini uretir.
 *
 * Saf metin uretimi yapar; hicbir I/O (ag, DB) icermez. Sablonlar sabittir
 * ancak icine context'teki gercek degerler gomulur (kisisellestirme).
 */
@Component
public class PromptBuilder {

    public String build(RecommendationContext ctx, TriggerType type) {
        return switch (type) {
            case QUOTA_80 -> buildQuota80(ctx);
            case QUOTA_100 -> buildQuota100(ctx);
            case ANOMALY -> buildAnomaly(ctx);
        };
    }

    private String buildQuota80(RecommendationContext ctx) {
        return "Bir ev enerji yönetim sistemi kullanıcısı için kişiselleştirilmiş enerji tasarrufu önerisi hazırla.\n\n"
                + "Durum: Evin harcaması aylık bütçe limitinin %80'ine ulaştı.\n"
                + "- Bütçe limiti: " + plain(ctx.budgetLimit()) + " TL\n"
                + "- Şu ana kadarki harcama: " + plain(ctx.accumulatedCost()) + " TL\n"
                + "- Kullanım oranı: %" + plain(ctx.usagePercent()) + "\n"
                + "- Toplam tüketim: " + plain(ctx.accumulatedKwh()) + " kWh\n"
                + "- Aktif tarife: " + tariff(ctx.activeTariff()) + "\n"
                + "- Ev adı: " + text(ctx.homeName()) + "\n"
                + "- Bu ev için daha önce üretilmiş öneri sayısı: " + ctx.priorRecommendationCount() + "\n\n"
                + "Kullanıcının ay sonunda bütçe limitini aşmasını önleyecek öneriler ver.\n\n"
                + instruction();
    }

    private String buildQuota100(RecommendationContext ctx) {
        return "Bir ev enerji yönetim sistemi kullanıcısı için kişiselleştirilmiş enerji tasarrufu önerisi hazırla.\n\n"
                + "Durum: Ev, aylık bütçe limitini aştı ve cezalı (yüksek) tarifeye geçti.\n"
                + "- Bütçe limiti: " + plain(ctx.budgetLimit()) + " TL\n"
                + "- Şu ana kadarki harcama: " + plain(ctx.accumulatedCost()) + " TL\n"
                + "- Kullanım oranı: %" + plain(ctx.usagePercent()) + "\n"
                + "- Toplam tüketim: " + plain(ctx.accumulatedKwh()) + " kWh\n"
                + "- Aktif tarife: " + tariff(ctx.activeTariff()) + "\n"
                + "- Ev adı: " + text(ctx.homeName()) + "\n"
                + "- Bu ev için daha önce üretilmiş öneri sayısı: " + ctx.priorRecommendationCount() + "\n\n"
                + "Kullanıcının bundan sonraki harcamasını ve cezalı tarifedeki maliyetini azaltacak öneriler ver.\n\n"
                + instruction();
    }

    private String buildAnomaly(RecommendationContext ctx) {
        return "Bir ev enerji yönetim sistemi kullanıcısı için kişiselleştirilmiş enerji tasarrufu önerisi hazırla.\n\n"
                + "Durum: Bir cihaz güvenli güç limitini üst üste aşarak anormal tüketim gösterdi.\n"
                + "- Cihazın son ölçülen gücü: " + plain(ctx.lastMeasuredWatt()) + " W\n"
                + "- Güvenli güç limiti: " + plain(ctx.safeWattLimit()) + " W\n"
                + "- Üst üste aşım sayısı: " + plainInt(ctx.consecutiveBreachCount()) + "\n"
                + "- Aktif tarife: " + tariff(ctx.activeTariff()) + "\n"
                + "- Ev adı: " + text(ctx.homeName()) + "\n"
                + "- Bu ev için daha önce üretilmiş öneri sayısı: " + ctx.priorRecommendationCount() + "\n\n"
                + "Kullanıcıya bu cihazın olası arıza/verimsizlik durumunu ve güvenli kullanımını hedefleyen öneriler ver.\n\n"
                + instruction();
    }

    private String instruction() {
        return "Yanıtın KESİNLİKLE Türkçe olsun. Kısa tut: en fazla 3-4 madde. "
                + "Somut, uygulanabilir ve davranışsal öneriler ver. "
                + "Düz metin yaz; markdown, başlık, yıldız (*) veya numara biçimlendirmesi kullanma.";
    }

    private String tariff(ActiveTariff activeTariff) {
        if (activeTariff == null) {
            return "-";
        }
        return activeTariff == ActiveTariff.PENALTY ? "Cezalı (yüksek) tarife" : "Normal tarife";
    }

    private String text(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String plain(BigDecimal value) {
        return value == null ? "-" : value.toPlainString();
    }

    private String plainInt(Integer value) {
        return value == null ? "-" : value.toString();
    }
}
