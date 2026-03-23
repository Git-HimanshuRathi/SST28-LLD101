package com.example.parking;

public class Gate {
    private final int gateNumber;
    private final int floor;

    public Gate(int gateNumber, int floor) {
        this.gateNumber = gateNumber; this.floor = floor;
    }

    public int gateNumber() { return gateNumber; }
    public int floor() { return floor; }
}
