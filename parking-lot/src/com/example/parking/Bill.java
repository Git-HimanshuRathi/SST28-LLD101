package com.example.parking;

public class Bill {
    private final ParkingTicket ticket;
    private final long exitTime;
    private final int hoursParked;
    private final double totalAmount;

    public Bill(ParkingTicket ticket, long exitTime, int hoursParked, double totalAmount) {
        this.ticket = ticket; this.exitTime = exitTime;
        this.hoursParked = hoursParked; this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Bill{ticket=" + ticket.ticketId() + ", vehicle=" + ticket.vehicle().licensePlate()
                + ", slot=" + ticket.slot() + ", hours=" + hoursParked
                + ", amount=" + String.format("%.2f", totalAmount) + "}";
    }
}
