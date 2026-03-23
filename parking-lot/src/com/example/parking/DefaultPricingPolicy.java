package com.example.parking;

import java.util.HashMap;
import java.util.Map;

public class DefaultPricingPolicy implements PricingPolicy {
    private final Map<SlotType, Double> rates = new HashMap<>();

    public DefaultPricingPolicy() {
        rates.put(SlotType.SMALL, 10.0);
        rates.put(SlotType.MEDIUM, 20.0);
        rates.put(SlotType.LARGE, 40.0);
    }

    @Override
    public double ratePerHour(SlotType slotType) {
        return rates.getOrDefault(slotType, 20.0);
    }
}
