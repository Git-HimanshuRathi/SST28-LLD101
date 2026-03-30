package com.example.elevator;

import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Elevator System ===\n");

        // --- Create 3 elevators for a 10-floor building ---
        int minFloor = 0, maxFloor = 10;
        List<Elevator> elevators = Arrays.asList(
                new Elevator(1, minFloor, maxFloor),
                new Elevator(2, minFloor, maxFloor),
                new Elevator(3, minFloor, maxFloor)
        );

        ElevatorController controller = new ElevatorController(elevators, new LookAheadStrategy());
        Building building = new Building("Tech Park Tower", maxFloor, controller);

        // --- Scenario 1: Simple hall call ---
        System.out.println("--- Scenario 1: Person on floor 5 wants to go UP ---");
        building.callElevator(5, Direction.UP);
        // Passenger enters and presses floor 8
        building.pressFloor(1, 8);
        building.simulate(9);

        // --- Scenario 2: Multiple simultaneous requests ---
        System.out.println("\n\n--- Scenario 2: Multiple hall calls ---");
        building.callElevator(3, Direction.UP);     // Person on floor 3 wants UP
        building.callElevator(7, Direction.DOWN);   // Person on floor 7 wants DOWN
        building.callElevator(1, Direction.UP);     // Person on floor 1 wants UP

        // After dispatching, passengers press their destinations
        building.pressFloor(1, 3);   // Elevator 1 is heading to floor 3
        building.pressFloor(2, 7);   // Elevator 2 is heading to floor 7
        building.pressFloor(3, 1);   // Elevator 3 is heading to floor 1

        // Simulate enough steps for all to reach
        building.simulate(10);

        // --- Scenario 3: Passengers inside press destinations ---
        System.out.println("\n\n--- Scenario 3: Cabin button presses after pickup ---");
        // After elevator 1 picks up at floor 3, passenger wants floor 9
        building.pressFloor(1, 9);
        // After elevator 2 picks up at floor 7, passenger wants floor 2
        building.pressFloor(2, 2);
        // After elevator 3 picks up at floor 1, passenger wants floor 6
        building.pressFloor(3, 6);

        building.simulate(10);

        System.out.println("\n=== Simulation Complete ===");
    }
}
