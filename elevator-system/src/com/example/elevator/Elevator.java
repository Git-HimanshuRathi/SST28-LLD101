package com.example.elevator;

import java.util.TreeSet;
import java.util.Collections;

/**
 * Represents a single elevator cart.
 *
 * Requirements addressed:
 * - Weight limit per cart (variable, different for different carts)
 * - Overweight → elevator stops, opens door, alarm played
 * - Alarm button → that particular elevator stops and rings alarm
 * - Open/Close door buttons inside elevator
 * - Emergency button inside
 * - State: UP, DOWN, IDLE, MAINTENANCE
 * - Inside button (floor) → controls only this individual cart
 */
public class Elevator {
    private final int id;
    private int currentFloor;
    private ElevatorState state;
    private DoorState doorState;
    private final TreeSet<Integer> upStops;      // floors to visit while going UP
    private final TreeSet<Integer> downStops;    // floors to visit while going DOWN
    private final int minFloor;
    private final int maxFloor;

    // ─── Weight management ──────────────────────────────────────
    private final double maxWeightKg;   // variable per cart (e.g. 700kg)
    private double currentWeightKg;     // current load in cart

    // ─── Alarm state ────────────────────────────────────────────
    private boolean alarmActive;

    public Elevator(int id, int minFloor, int maxFloor, double maxWeightKg) {
        this.id = id;
        this.currentFloor = minFloor;
        this.state = ElevatorState.IDLE;
        this.doorState = DoorState.CLOSED;
        this.upStops = new TreeSet<>();
        this.downStops = new TreeSet<>(Collections.reverseOrder());
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.maxWeightKg = maxWeightKg;
        this.currentWeightKg = 0;
        this.alarmActive = false;
    }

    // ─── Getters ────────────────────────────────────────────────
    public int id() { return id; }
    public int currentFloor() { return currentFloor; }
    public ElevatorState state() { return state; }
    public DoorState doorState() { return doorState; }
    public int minFloor() { return minFloor; }
    public int maxFloor() { return maxFloor; }
    public double maxWeightKg() { return maxWeightKg; }
    public double currentWeightKg() { return currentWeightKg; }
    public boolean isAlarmActive() { return alarmActive; }

    // ─── Inside button: floor (controls only this cart) ─────────
    /** Inside panel floor button pressed — adds stop for this elevator only. */
    public void addStop(int floor) {
        if (state == ElevatorState.MAINTENANCE) {
            System.out.println("  ✗ Elevator-" + id + ": under MAINTENANCE, ignoring request");
            return;
        }
        if (alarmActive) {
            System.out.println("  ✗ Elevator-" + id + ": alarm is active, ignoring request");
            return;
        }
        if (floor < minFloor || floor > maxFloor) {
            System.out.println("  ✗ Elevator-" + id + ": floor " + floor + " out of range");
            return;
        }
        if (floor == currentFloor && state == ElevatorState.IDLE) {
            System.out.println("  → Elevator-" + id + ": already at floor " + floor + ", opening doors");
            openDoor();
            return;
        }
        if (floor > currentFloor) {
            upStops.add(floor);
        } else if (floor < currentFloor) {
            downStops.add(floor);
        } else {
            // Same floor while moving — stop here next opportunity
            openDoor();
            return;
        }

        // If idle, start moving
        if (state == ElevatorState.IDLE) {
            state = (floor > currentFloor) ? ElevatorState.UP : ElevatorState.DOWN;
        }
    }

    // ─── Weight management ──────────────────────────────────────
    /**
     * Update the current weight in the elevator.
     * If overweight → elevator stops, opens door, alarm played.
     * Returns true if overweight condition triggered.
     */
    public boolean updateWeight(double newWeightKg) {
        this.currentWeightKg = newWeightKg;
        if (currentWeightKg > maxWeightKg) {
            System.out.println("  ⚠ Elevator-" + id + ": OVERWEIGHT! ("
                    + currentWeightKg + "kg > " + maxWeightKg + "kg limit)"
                    + " → stopping, opening door, alarm playing");
            // Elevator stops, opens door, alarm played
            stopElevator();
            openDoor();
            alarmActive = true;
            return true;
        }
        // If weight goes back under limit and alarm was due to overweight, reset
        if (alarmActive && currentWeightKg <= maxWeightKg) {
            System.out.println("  ✔ Elevator-" + id + ": weight normalized, alarm cleared");
            alarmActive = false;
        }
        return false;
    }

    // ─── Inside button: Alarm ───────────────────────────────────
    /** Alarm button pressed — that particular elevator stops and rings alarm. */
    public void pressAlarm() {
        System.out.println("  🚨 Elevator-" + id + ": ALARM pressed! Stopping and ringing alarm.");
        alarmActive = true;
        stopElevator();
    }

    /** Reset alarm (by maintenance/operator). */
    public void resetAlarm() {
        if (!alarmActive) return;
        System.out.println("  ✔ Elevator-" + id + ": alarm reset by operator");
        alarmActive = false;
    }

    // ─── Inside button: Open door ───────────────────────────────
    /** Open door button pressed inside elevator. */
    public void pressOpenDoor() {
        if (state == ElevatorState.UP || state == ElevatorState.DOWN) {
            System.out.println("  ✗ Elevator-" + id + ": cannot open door while moving");
            return;
        }
        openDoor();
        System.out.println("  → Elevator-" + id + ": door opened (button pressed)");
    }

    // ─── Inside button: Close door ──────────────────────────────
    /** Close door button pressed inside elevator. */
    public void pressCloseDoor() {
        if (doorState == DoorState.CLOSED) return;
        closeDoor();
        System.out.println("  → Elevator-" + id + ": door closed (button pressed)");
    }

    // ─── Inside button: Emergency ───────────────────────────────
    /** Emergency button pressed — stops elevator and opens door. */
    public void pressEmergency() {
        System.out.println("  🆘 Elevator-" + id + ": EMERGENCY pressed! Stopping and opening door.");
        stopElevator();
        openDoor();
        alarmActive = true;
    }

    // ─── Maintenance mode (handled by operator) ─────────────────
    /** Put elevator under maintenance — will not move up and down. */
    public void setMaintenance() {
        System.out.println("  🔧 Elevator-" + id + ": entering MAINTENANCE mode");
        state = ElevatorState.MAINTENANCE;
        upStops.clear();
        downStops.clear();
        if (doorState == DoorState.OPEN) closeDoor();
    }

    /** Take elevator out of maintenance — returns to IDLE. */
    public void clearMaintenance() {
        if (state != ElevatorState.MAINTENANCE) return;
        System.out.println("  ✔ Elevator-" + id + ": exiting MAINTENANCE, now IDLE");
        state = ElevatorState.IDLE;
        alarmActive = false;
    }

    // ─── Movement ───────────────────────────────────────────────
    /** Move elevator one floor in current direction. Returns true if a stop was made. */
    public boolean moveOneStep() {
        if (state == ElevatorState.MAINTENANCE) return false;
        if (alarmActive) return false;
        if (doorState == DoorState.OPEN) closeDoor();
        if (state == ElevatorState.IDLE) return false;

        // Move one floor (clamped to min/max bounds)
        if (state == ElevatorState.UP) {
            if (currentFloor >= maxFloor) {
                updateState();
                return false;
            }
            currentFloor++;
        } else if (state == ElevatorState.DOWN) {
            if (currentFloor <= minFloor) {
                updateState();
                return false;
            }
            currentFloor--;
        }

        // Check if current floor is a stop
        boolean stopped = false;
        if (state == ElevatorState.UP && upStops.contains(currentFloor)) {
            upStops.remove(currentFloor);
            openDoor();
            stopped = true;
        } else if (state == ElevatorState.DOWN && downStops.contains(currentFloor)) {
            downStops.remove(currentFloor);
            openDoor();
            stopped = true;
        }

        updateState();
        return stopped;
    }

    private void stopElevator() {
        if (state == ElevatorState.UP || state == ElevatorState.DOWN) {
            state = ElevatorState.IDLE;
        }
    }

    private void updateState() {
        if (state == ElevatorState.UP) {
            if (upStops.isEmpty()) {
                state = downStops.isEmpty() ? ElevatorState.IDLE : ElevatorState.DOWN;
            }
        } else if (state == ElevatorState.DOWN) {
            if (downStops.isEmpty()) {
                state = upStops.isEmpty() ? ElevatorState.IDLE : ElevatorState.UP;
            }
        }
    }

    public void openDoor() { doorState = DoorState.OPEN; }
    public void closeDoor() { doorState = DoorState.CLOSED; }
    public boolean hasStops() { return !upStops.isEmpty() || !downStops.isEmpty(); }
    public int stopsCount() { return upStops.size() + downStops.size(); }

    /** Check if this elevator is available (not in maintenance, not alarming). */
    public boolean isAvailable() {
        return state != ElevatorState.MAINTENANCE && !alarmActive;
    }

    @Override
    public String toString() {
        return "Elevator-" + id + " [floor=" + currentFloor + ", state=" + state
                + ", door=" + doorState + ", weight=" + currentWeightKg + "/" + maxWeightKg + "kg"
                + (alarmActive ? ", ⚠ALARM" : "")
                + ", upStops=" + upStops + ", downStops=" + downStops + "]";
    }
}
