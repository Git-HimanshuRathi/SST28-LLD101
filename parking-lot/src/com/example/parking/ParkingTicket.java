package com.example.parking;

public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSlot slot;
    private final long entryTime;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSlot slot, long entryTime) {
        this.ticketId = ticketId; this.vehicle = vehicle; this.slot = slot; this.entryTime = entryTime;
    }

    public String ticketId() { return ticketId; }
    public Vehicle vehicle() { return vehicle; }
    public ParkingSlot slot() { return slot; }
    public long entryTime() { return entryTime; }
}
