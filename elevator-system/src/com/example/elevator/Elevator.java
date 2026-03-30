package com.example.elevator;

import java.util.TreeSet;
import java.util.Collections;

public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private DoorState doorState;
    private final TreeSet<Integer> upStops;      // floors to visit while going UP
    private final TreeSet<Integer> downStops;    // floors to visit while going DOWN
    private final int minFloor;
    private final int maxFloor;

    public Elevator(int id, int minFloor, int maxFloor) {
        this.id = id;
        this.currentFloor = minFloor;
        this.direction = Direction.IDLE;
        this.doorState = DoorState.CLOSED;
        this.upStops = new TreeSet<>();
        this.downStops = new TreeSet<>(Collections.reverseOrder());
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    public int id() { return id; }
    public int currentFloor() { return currentFloor; }
    public Direction direction() { return direction; }
    public DoorState doorState() { return doorState; }
    public int minFloor() { return minFloor; }
    public int maxFloor() { return maxFloor; }

    /** Add a destination floor. Automatically placed in the correct stop set. */
    public void addStop(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            System.out.println("  ✗ Elevator-" + id + ": floor " + floor + " out of range");
            return;
        }
        if (floor == currentFloor && direction == Direction.IDLE) {
            System.out.println("  → Elevator-" + id + ": already at floor " + floor + ", opening doors");
            openDoor();
            return;
        }
        if (floor > currentFloor) {
            upStops.add(floor);
        } else if (floor < currentFloor) {
            downStops.add(floor);
        } else {
            // Same floor, add to current direction's stop set
            if (direction == Direction.UP) upStops.add(floor);
            else downStops.add(floor);
        }

        // If idle, start moving
        if (direction == Direction.IDLE) {
            direction = (floor > currentFloor) ? Direction.UP : Direction.DOWN;
        }
    }

    /** Move elevator one floor in current direction. Returns true if a stop was made. */
    public boolean moveOneStep() {
        if (doorState == DoorState.OPEN) closeDoor();
        if (direction == Direction.IDLE) return false;

        // Move one floor
        if (direction == Direction.UP) {
            currentFloor++;
        } else {
            currentFloor--;
        }

        // Check if current floor is a stop
        boolean stopped = false;
        if (direction == Direction.UP && upStops.contains(currentFloor)) {
            upStops.remove(currentFloor);
            openDoor();
            stopped = true;
        } else if (direction == Direction.DOWN && downStops.contains(currentFloor)) {
            downStops.remove(currentFloor);
            openDoor();
            stopped = true;
        }

        // Decide next direction
        updateDirection();
        return stopped;
    }

    private void updateDirection() {
        if (direction == Direction.UP) {
            if (upStops.isEmpty()) {
                // Switch to DOWN if there are down stops, else IDLE
                direction = downStops.isEmpty() ? Direction.IDLE : Direction.DOWN;
            }
        } else if (direction == Direction.DOWN) {
            if (downStops.isEmpty()) {
                direction = upStops.isEmpty() ? Direction.IDLE : Direction.UP;
            }
        }
    }

    public void openDoor() { doorState = DoorState.OPEN; }
    public void closeDoor() { doorState = DoorState.CLOSED; }
    public boolean hasStops() { return !upStops.isEmpty() || !downStops.isEmpty(); }
    public int stopsCount() { return upStops.size() + downStops.size(); }

    @Override
    public String toString() {
        return "Elevator-" + id + " [floor=" + currentFloor + ", dir=" + direction
                + ", door=" + doorState + ", upStops=" + upStops + ", downStops=" + downStops + "]";
    }
}
