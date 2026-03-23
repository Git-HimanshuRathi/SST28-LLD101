package com.example.parking;

public class BillingService {
    private final PricingPolicy policy;

    public BillingService(PricingPolicy policy) { this.policy = policy; }

    public Bill generateBill(ParkingTicket ticket, long exitTime) {
        int hours = Math.max(1, (int) Math.ceil((exitTime - ticket.entryTime()) / 3600000.0));
        double amount = hours * policy.ratePerHour(ticket.slot().type());
        return new Bill(ticket, exitTime, hours, amount);
    }
}
