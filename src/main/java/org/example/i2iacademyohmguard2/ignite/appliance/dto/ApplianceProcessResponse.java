package org.example.i2iacademyohmguard2.ignite.appliance.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplianceProcessResponse(
        UUID applianceId,
        UUID homeId,
        BigDecimal measuredWatt,
        BigDecimal safeWattLimit,
        int breachCount,
        boolean anomalyTriggered
) {
}
