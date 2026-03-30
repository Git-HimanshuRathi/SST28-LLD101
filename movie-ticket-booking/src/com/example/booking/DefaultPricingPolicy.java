package com.example.booking;

import java.util.*;

/**
 * Base pricing + composable PricingRules.
 * Base price is fixed per seat type. Additional rules (weekend, peak-hour, etc.)
 * are applied on top via the Strategy pattern.
 */
public class DefaultPricingPolicy implements PricingPolicy {
    private final Map<SeatType, Double> basePrices = new HashMap<>();
    private final List<PricingRule> rules = new ArrayList<>();

    public DefaultPricingPolicy() {
        basePrices.put(SeatType.SILVER, 150.0);
        basePrices.put(SeatType.GOLD, 250.0);
        basePrices.put(SeatType.PLATINUM, 400.0);
    }

    /** Add a dynamic pricing rule. */
    public void addRule(PricingRule rule) {
        rules.add(rule);
    }

    @Override
    public double getPrice(SeatType seatType) {
        return basePrices.getOrDefault(seatType, 200.0);
    }

    /**
     * Calculate final price for a seat in a given show.
     * Applies base price + all pricing rules (surcharges).
     */
    public double getFinalPrice(Show show, SeatType seatType) {
        double base = getPrice(seatType);
        double surcharge = 0;
        for (PricingRule rule : rules) {
            surcharge += rule.apply(show, seatType, base);
        }
        return base + surcharge;
    }
}
