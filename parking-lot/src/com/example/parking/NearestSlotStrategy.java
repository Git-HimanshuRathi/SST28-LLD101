package com.example.parking;

import java.util.List;

public class NearestSlotStrategy implements SlotAssignmentStrategy {
    @Override
    public ParkingSlot findSlot(List<ParkingSlot> slots, VehicleType vehicleType, Gate entryGate) {
        List<SlotType> compatible = VehicleSlotCompatibility.compatibleSlots(vehicleType);
        ParkingSlot best = null;
        int bestDist = Integer.MAX_VALUE;
        for (ParkingSlot s : slots) {
            if (!s.isOccupied() && compatible.contains(s.type())) {
                int dist = Math.abs(s.floor() - entryGate.floor()) * 100 + s.slotNumber();
                if (dist < bestDist) { bestDist = dist; best = s; }
            }
        }
        return best;
    }
}
