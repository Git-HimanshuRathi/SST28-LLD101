package com.example.elevator;

import java.util.*;

public class ElevatorController {
    private final List<Elevator> elevators;
    private final ElevatorSelectionStrategy strategy;
    private final Queue<Request> pendingRequests = new LinkedList<>();

    public ElevatorController(List<Elevator> elevators, ElevatorSelectionStrategy strategy) {
        this.elevators = Collections.unmodifiableList(new ArrayList<>(elevators));
        this.strategy = strategy;
    }

    /** External hall call: someone presses UP/DOWN on a floor. */
    public void requestElevator(int floor, Direction direction) {
        System.out.println("  ▶ Hall call: floor " + floor + " direction " + direction);
        Elevator chosen = strategy.selectElevator(elevators, floor, direction);
        if (chosen == null) {
            System.out.println("    ✗ No elevator available, queuing request");
            pendingRequests.add(new Request(floor, direction, RequestType.EXTERNAL));
            return;
        }
        System.out.println("    → Dispatching Elevator-" + chosen.id());
        chosen.addStop(floor);
    }

    /** Internal cabin button: passenger inside elevator presses a floor button. */
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

    /** Advance all elevators by one step. Process any pending requests with idle elevators. */
    public void step() {
        // Try to dispatch pending requests to idle elevators
        Iterator<Request> it = pendingRequests.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            Elevator chosen = strategy.selectElevator(elevators, r.floor(), r.direction());
            if (chosen != null && chosen.direction() == Direction.IDLE) {
                System.out.println("    → Dispatching pending request to Elevator-" + chosen.id()
                        + " for floor " + r.floor());
                chosen.addStop(r.floor());
                it.remove();
            }
        }

        // Move each elevator one step
        for (Elevator e : elevators) {
            if (e.hasStops() || e.direction() != Direction.IDLE) {
                boolean stopped = e.moveOneStep();
                if (stopped) {
                    System.out.println("    ■ Elevator-" + e.id() + " stopped at floor " + e.currentFloor());
                }
            }
        }
    }

    /** Print status of all elevators. */
    public void status() {
        for (Elevator e : elevators) System.out.println("    " + e);
    }
}
