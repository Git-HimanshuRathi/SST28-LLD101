package com.example.parking;

public class ParkingSlot {
    private final int slotNumber;
    private final SlotType type;
    private final int floor;
    private boolean occupied;

    public ParkingSlot(int slotNumber, SlotType type, int floor) {
        this.slotNumber = slotNumber; this.type = type; this.floor = floor;
    }

    public int slotNumber() { return slotNumber; }
    public SlotType type() { return type; }
    public int floor() { return floor; }
    public boolean isOccupied() { return occupied; }
    public void occupy() { occupied = true; }
    public void free() { occupied = false; }

    @Override
    public String toString() { return "Slot-" + floor + "-" + slotNumber + "(" + type + ")"; }
}
