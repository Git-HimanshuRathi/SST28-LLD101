package com.example.booking;

/**
 * Strategy interface for refund calculation on cancellation.
 */
public interface RefundPolicy {
    /** Calculate refund amount for a cancelled booking. */
    double calculateRefund(Booking booking);
}
