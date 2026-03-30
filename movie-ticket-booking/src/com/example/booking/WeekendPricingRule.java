package com.example.booking;

import java.util.Calendar;

/**
 * Adds a weekend surcharge (e.g., +20% on Saturday/Sunday).
 * Demonstrates the pricing rules/strategy pattern.
 */
public class WeekendPricingRule implements PricingRule {
    private final double surchargePercent;

    public WeekendPricingRule(double surchargePercent) {
        this.surchargePercent = surchargePercent;
    }

    @Override
    public double apply(Show show, SeatType seatType, double currentPrice) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(show.startTime());
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            return currentPrice * surchargePercent / 100.0;
        }
        return 0;
    }
}
