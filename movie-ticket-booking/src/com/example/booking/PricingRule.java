package com.example.booking;

/**
 * Strategy interface for dynamic pricing rules.
 * Multiple rules can be composed to adjust the base price.
 * Examples: weekend surcharge, peak-hour surcharge, demand-based pricing.
 */
public interface PricingRule {
    /** Return additional surcharge amount for the given show/seat. */
    double apply(Show show, SeatType seatType, double currentPrice);
}
