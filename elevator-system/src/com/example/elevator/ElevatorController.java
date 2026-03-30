package com.example.elevator;

import java.util.*;

/**
 * Controls all elevator carts in the building.
 *
 * Requirements addressed:
 * - Outside buttons (up, down) — controls ALL carts (dispatches one)
 * - Inside button (floor) — controls individual cart only
 * - Skips elevators in MAINTENANCE or with active alarm
 */
public class ElevatorController {
    private final List<Elevator> elevators;
    private final ElevatorSelectionStrategy strategy;
    private final Queue<Request> pendingRequests = new LinkedList<>();

    public ElevatorController(List<Elevator> elevators, ElevatorSelectionStrategy strategy) {
        this.elevators = Collections.unmodifiableList(new ArrayList<>(elevators));
        this.strategy = strategy;
    }

    /**
     * External hall call: someone on a floor presses UP/DOWN button.
     * Outside button controls all carts — the controller picks ONE to dispatch.
     * Only considers available elevators (not in maintenance, not alarming).
     */
    public void requestElevator(int floor, Direction direction) {
        System.out.println("  ▶ Hall call: floor " + floor + " direction " + direction);

        // Filter to only available elevators
        List<Elevator> available = new ArrayList<>();
        for (Elevator e : elevators) {
            if (e.isAvailable()) available.add(e);
        }

        if (available.isEmpty()) {
            System.out.println("    ✗ No elevator available, queuing request");
            pendingRequests.add(new Request(floor, direction, RequestType.EXTERNAL));
            return;
        }

        Elevator chosen = strategy.selectElevator(available, floor, direction);
        if (chosen == null) {
            System.out.println("    ✗ No suitable elevator, queuing request");
            pendingRequests.add(new Request(floor, direction, RequestType.EXTERNAL));
            return;
        }
        System.out.println("    → Dispatching Elevator-" + chosen.id());
        chosen.addStop(floor);
    }

    /**
     * Internal cabin button: passenger inside elevator presses a floor button.
     * Inside button controls only that individual cart.
     */
    public void pressFloor(int elevatorId, int floor) {
        System.out.println("  ▶ Cabin button: Elevator-" + elevatorId + " → floor " + floor);
        for (Elevator e : elevators) {
            if (e.id() == elevatorId) {
                e.addStop(floor);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown elevator: " + elevatorId);
    }

    /** Inside panel: open door button (only for a specific elevator). */
    public void pressOpenDoor(int elevatorId) {
        getElevator(elevatorId).pressOpenDoor();
    }

    /** Inside panel: close door button (only for a specific elevator). */
    public void pressCloseDoor(int elevatorId) {
        getElevator(elevatorId).pressCloseDoor();
    }

    /** Inside panel: emergency button (only for a specific elevator). */
    public void pressEmergency(int elevatorId) {
        getElevator(elevatorId).pressEmergency();
    }

    /** Inside panel: alarm button (only for a specific elevator). */
    public void pressAlarm(int elevatorId) {
        getElevator(elevatorId).pressAlarm();
    }

    /** Operator: put elevator under maintenance. */
    public void setMaintenance(int elevatorId) {
        getElevator(elevatorId).setMaintenance();
    }

    /** Operator: clear maintenance for an elevator. */
    public void clearMaintenance(int elevatorId) {
        getElevator(elevatorId).clearMaintenance();
    }

    /** Operator: reset alarm for an elevator. */
    public void resetAlarm(int elevatorId) {
        getElevator(elevatorId).resetAlarm();
    }

    /** Update weight sensor reading for an elevator. */
    public boolean updateWeight(int elevatorId, double weightKg) {
        return getElevator(elevatorId).updateWeight(weightKg);
    }

    /** Advance all elevators by one step. Process any pending requests with idle elevators. */
    public void step() {
        // Try to dispatch pending requests to available elevators
        Iterator<Request> it = pendingRequests.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            List<Elevator> available = new ArrayList<>();
            for (Elevator e : elevators) {
                if (e.isAvailable()) available.add(e);
            }
            Elevator chosen = strategy.selectElevator(available, r.floor(), r.direction());
            if (chosen != null && chosen.state() == ElevatorState.IDLE) {
                System.out.println("    → Dispatching pending request to Elevator-" + chosen.id()
                        + " for floor " + r.floor());
                chosen.addStop(r.floor());
                it.remove();
            }
        }

        // Move each available elevator one step
        for (Elevator e : elevators) {
            if (e.isAvailable() && (e.hasStops() || e.state() != ElevatorState.IDLE)) {
                boolean stopped = e.moveOneStep();
                if (stopped) {
                    System.out.println("    ■ Elevator-" + e.id()
                            + " stopped at floor " + e.currentFloor());
                }
            }
        }
    }

    /** Print status of all elevators. */
    public void status() {
        for (Elevator e : elevators) System.out.println("    " + e);
    }

    public List<Elevator> elevators() { return elevators; }

    private Elevator getElevator(int elevatorId) {
        for (Elevator e : elevators) {
            if (e.id() == elevatorId) return e;
        }
        throw new IllegalArgumentException("Unknown elevator: " + elevatorId);
    }
}
