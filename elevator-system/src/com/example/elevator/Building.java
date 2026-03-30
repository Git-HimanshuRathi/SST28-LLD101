package com.example.elevator;

import java.util.*;

/**
 * Represents the building with elevators.
 * Provides user-facing controls:
 *  - Hall buttons (UP/DOWN on every floor) — controls ALL carts
 *  - Inside buttons (floor, open, close, emergency, alarm) — controls individual cart
 *  - Operator controls (maintenance)
 */
public class Building {
    private final String name;
    private final int totalFloors;
    private final ElevatorController controller;

    public Building(String name, int totalFloors, ElevatorController controller) {
        this.name = name;
        this.totalFloors = totalFloors;
        this.controller = controller;
    }

    public String name() { return name; }
    public int totalFloors() { return totalFloors; }
    public ElevatorController controller() { return controller; }

    // ─── Outside buttons (on every floor) — controls all carts ──
    /** Hall call: person on a floor presses UP or DOWN. Dispatches to one cart. */
    public void callElevator(int floor, Direction direction) {
        controller.requestElevator(floor, direction);
    }

    // ─── Inside buttons — controls individual cart ──────────────
    /** Floor button inside elevator — only moves that specific cart. */
    public void pressFloor(int elevatorId, int floor) {
        controller.pressFloor(elevatorId, floor);
    }

    /** Open door button inside elevator. */
    public void pressOpenDoor(int elevatorId) {
        controller.pressOpenDoor(elevatorId);
    }

    /** Close door button inside elevator. */
    public void pressCloseDoor(int elevatorId) {
        controller.pressCloseDoor(elevatorId);
    }

    /** Emergency button inside elevator — stops and opens door. */
    public void pressEmergency(int elevatorId) {
        controller.pressEmergency(elevatorId);
    }

    /** Alarm button inside elevator — that particular elevator stops and rings alarm. */
    public void pressAlarm(int elevatorId) {
        controller.pressAlarm(elevatorId);
    }

    // ─── Weight sensor ──────────────────────────────────────────
    /** Simulate weight change in elevator (sensor reading). */
    public boolean updateWeight(int elevatorId, double weightKg) {
        return controller.updateWeight(elevatorId, weightKg);
    }

    // ─── Operator controls (maintenance) ────────────────────────
    /** Put elevator under maintenance — will not move. Handled by operator. */
    public void setMaintenance(int elevatorId) {
        controller.setMaintenance(elevatorId);
    }

    /** Clear maintenance — elevator returns to IDLE. */
    public void clearMaintenance(int elevatorId) {
        controller.clearMaintenance(elevatorId);
    }

    /** Reset alarm (operator). */
    public void resetAlarm(int elevatorId) {
        controller.resetAlarm(elevatorId);
    }

    // ─── Simulation ─────────────────────────────────────────────
    /** Run the simulation for the given number of steps. */
    public void simulate(int steps) {
        for (int i = 1; i <= steps; i++) {
            System.out.println("\n  ─── Step " + i + " ───");
            controller.step();
            controller.status();
        }
    }
}
