package com.example.booking;

public interface PricingPolicy {
    double getPrice(SeatType seatType);
}
