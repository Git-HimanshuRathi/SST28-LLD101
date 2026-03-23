package com.example.parking;

import java.util.*;

public class ParkingLot {
    private final List<ParkingSlot> slots;
    private final SlotAssignmentStrategy strategy;
    private final BillingService billing;
    private final Map<String, ParkingTicket> activeTickets = new HashMap<>();
    private int ticketCounter = 0;

    public ParkingLot(List<ParkingSlot> slots, SlotAssignmentStrategy strategy, BillingService billing) {
        this.slots = slots; this.strategy = strategy; this.billing = billing;
    }

    public ParkingTicket entry(Vehicle vehicle, Gate gate, long time) {
        ParkingSlot slot = strategy.findSlot(slots, vehicle.type(), gate);
        if (slot == null) {
            System.out.println("No slot available for " + vehicle.licensePlate());
            return null;
        }
        slot.occupy();
        String id = "T-" + (++ticketCounter);
        ParkingTicket ticket = new ParkingTicket(id, vehicle, slot, time);
        activeTickets.put(id, ticket);
        System.out.println("Entry: " + vehicle.licensePlate() + " -> " + slot + " | ticket=" + id);
        return ticket;
    }

    public Bill exit(String ticketId, long time) {
        ParkingTicket ticket = activeTickets.remove(ticketId);
        if (ticket == null) throw new IllegalArgumentException("Unknown ticket: " + ticketId);
        ticket.slot().free();
        Bill bill = billing.generateBill(ticket, time);
        System.out.println("Exit: " + bill);
        return bill;
    }
}
