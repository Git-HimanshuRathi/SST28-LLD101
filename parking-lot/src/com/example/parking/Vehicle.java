package com.example.parking;

public class Vehicle {
    private final String licensePlate;
    private final VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate; this.type = type;
    }

    public String licensePlate() { return licensePlate; }
    public VehicleType type() { return type; }
}
