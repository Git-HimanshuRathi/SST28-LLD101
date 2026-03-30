package com.example.booking;

import java.util.Calendar;

/**
 * Adds a surcharge during peak hours (e.g., 6 PM - 10 PM).
 * Another composable pricing rule.
 */
public class PeakHourPricingRule implements PricingRule {
    private final int peakStartHour;  // inclusive
    private final int peakEndHour;    // exclusive
    private final double surchargeAmount;

    public PeakHourPricingRule(int peakStartHour, int peakEndHour, double surchargeAmount) {
        this.peakStartHour = peakStartHour;
        this.peakEndHour = peakEndHour;
        this.surchargeAmount = surchargeAmount;
    }

    @Override
    public double apply(Show show, SeatType seatType, double currentPrice) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(show.startTime());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= peakStartHour && hour < peakEndHour) {
            return surchargeAmount;
        }
        return 0;
    }
}
