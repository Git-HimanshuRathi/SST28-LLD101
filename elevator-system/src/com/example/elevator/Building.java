package com.example.elevator;

import java.util.*;

public class Building {
    private final String name;
    private final int totalFloors;
    private final ElevatorController controller;

    public Building(String name, int totalFloors, ElevatorController controller) {
        this.name = name; this.totalFloors = totalFloors; this.controller = controller;
    }

    public String name() { return name; }
    public int totalFloors() { return totalFloors; }
    public ElevatorController controller() { return controller; }

    /** Hall call: a person on a floor presses UP or DOWN. */
    public void callElevator(int floor, Direction direction) {
        controller.requestElevator(floor, direction);
    }

    /** Cabin button: a person inside elevator presses a destination floor. */
    public void pressFloor(int elevatorId, int floor) {
        controller.pressFloor(elevatorId, floor);
    }

    /** Run the simulation for the given number of steps. */
    public void simulate(int steps) {
        for (int i = 1; i <= steps; i++) {
            System.out.println("\n  ─── Step " + i + " ───");
            controller.step();
            controller.status();
        }
    }
}
