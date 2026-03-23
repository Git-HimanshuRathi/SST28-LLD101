package com.example.parking;

public interface PricingPolicy {
    double ratePerHour(SlotType slotType);
}
