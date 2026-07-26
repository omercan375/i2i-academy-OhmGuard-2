package org.example.i2iacademyohmguard2.ignite.home.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record ProcessResponse(UUID homeId, BigDecimal newAccumulatedKwh, BigDecimal newAccumulatedCost, Boolean alert80, boolean alert100,boolean alert80Previous,boolean alert100Previous,int quotaVersion) {
}
