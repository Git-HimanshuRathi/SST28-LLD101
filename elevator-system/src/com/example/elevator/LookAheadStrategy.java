package com.example.elevator;

import java.util.List;

/**
 * LOOK-based elevator selection.
 * Now filters only available elevators (not in maintenance/alarm).
 *
 * 1. Prefer an elevator already moving toward the requested floor in the same direction.
 * 2. Among candidates, pick the closest one.
 * 3. If no same-direction elevator, pick the closest idle elevator.
 * 4. Fallback: pick the elevator with the fewest pending stops.
 */
public class LookAheadStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor, Direction direction) {
        Elevator best = null;
        int bestScore = Integer.MAX_VALUE;

        // Pass 1: elevator going in the same direction and can pick up on the way
        for (Elevator e : elevators) {
            if (!e.isAvailable()) continue;
            ElevatorState eState = e.state();
            boolean sameDir = (direction == Direction.UP && eState == ElevatorState.UP)
                    || (direction == Direction.DOWN && eState == ElevatorState.DOWN);
            if (sameDir) {
                boolean canPickUp = (direction == Direction.UP && e.currentFloor() <= floor)
                        || (direction == Direction.DOWN && e.currentFloor() >= floor);
                if (canPickUp) {
                    int dist = Math.abs(e.currentFloor() - floor);
                    if (dist < bestScore) { bestScore = dist; best = e; }
                }
            }
        }
        if (best != null) return best;

        // Pass 2: idle elevators, closest wins
        bestScore = Integer.MAX_VALUE;
        for (Elevator e : elevators) {
            if (!e.isAvailable()) continue;
            if (e.state() == ElevatorState.IDLE) {
                int dist = Math.abs(e.currentFloor() - floor);
                if (dist < bestScore) { bestScore = dist; best = e; }
            }
        }
        if (best != null) return best;

        // Pass 3: fallback – least busy available elevator
        bestScore = Integer.MAX_VALUE;
        for (Elevator e : elevators) {
            if (!e.isAvailable()) continue;
            if (e.stopsCount() < bestScore) { bestScore = e.stopsCount(); best = e; }
        }
        return best;
    }
}
