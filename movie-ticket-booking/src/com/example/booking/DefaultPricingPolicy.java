package com.example.booking;

import java.util.HashMap;
import java.util.Map;

public class DefaultPricingPolicy implements PricingPolicy {
    private final Map<SeatType, Double> prices = new HashMap<>();

    public DefaultPricingPolicy() {
        prices.put(SeatType.SILVER, 150.0);
        prices.put(SeatType.GOLD, 250.0);
        prices.put(SeatType.PLATINUM, 400.0);
    }

    @Override
    public double getPrice(SeatType seatType) {
        return prices.getOrDefault(seatType, 200.0);
    }
}
