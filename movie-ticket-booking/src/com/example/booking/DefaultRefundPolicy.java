package com.example.booking;

/**
 * Default refund policy: full refund if cancelled > 2 hrs before show,
 * 50% refund otherwise.
 */
public class DefaultRefundPolicy implements RefundPolicy {
    @Override
    public double calculateRefund(Booking booking) {
        long timeToShow = booking.show().startTime() - System.currentTimeMillis();
        long twoHoursMs = 2 * 60 * 60 * 1000L;
        if (timeToShow > twoHoursMs) {
            return booking.totalAmount(); // full refund
        } else {
            return booking.totalAmount() * 0.50; // 50% refund
        }
    }
}
