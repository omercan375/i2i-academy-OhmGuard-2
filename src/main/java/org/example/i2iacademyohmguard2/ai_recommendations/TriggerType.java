package org.example.i2iacademyohmguard2.ai_recommendations;

/**
 * Bir AI onerisini tetikleyen alarm turu.
 *
 * Sabit isimleri dogrudan {@code ai_recommendations.trigger_type} kolonuna
 * (EnumType.STRING) yazilir.
 */
public enum TriggerType {
    QUOTA_80,
    QUOTA_100,
    ANOMALY
}
