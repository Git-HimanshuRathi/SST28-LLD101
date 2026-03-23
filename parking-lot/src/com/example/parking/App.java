package com.example.parking;

import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Parking Lot ===");

        List<ParkingSlot> slots = Arrays.asList(
                new ParkingSlot(1, SlotType.SMALL, 1),
                new ParkingSlot(2, SlotType.MEDIUM, 1),
                new ParkingSlot(3, SlotType.LARGE, 1),
                new ParkingSlot(4, SlotType.MEDIUM, 2),
                new ParkingSlot(5, SlotType.SMALL, 2)
        );

        ParkingLot lot = new ParkingLot(slots, new NearestSlotStrategy(),
                new BillingService(new DefaultPricingPolicy()));

        Gate gate = new Gate(1, 1);
        long now = System.currentTimeMillis();

        ParkingTicket t1 = lot.entry(new Vehicle("KA-01-1234", VehicleType.CAR), gate, now);
        ParkingTicket t2 = lot.entry(new Vehicle("KA-02-5678", VehicleType.TWO_WHEELER), gate, now);
        ParkingTicket t3 = lot.entry(new Vehicle("KA-03-9999", VehicleType.BUS), gate, now);

        lot.exit(t1.ticketId(), now + 7200000);
        lot.exit(t2.ticketId(), now + 3600000);
        lot.exit(t3.ticketId(), now + 10800000);
    }
}
