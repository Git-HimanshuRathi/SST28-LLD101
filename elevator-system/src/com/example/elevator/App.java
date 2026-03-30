package com.example.elevator;

import java.util.*;

/**
 * Demo application for the Elevator System showing all requirements:
 *
 * 1. Outside buttons (UP/DOWN) — controls all carts, on every floor
 * 2. Inside buttons: floor, open, close, emergency — only 1 elevator
 * 3. Inside alarm button — that particular elevator stops and rings alarm
 * 4. Weight limit per cart (variable) — overweight → stops, opens, alarm
 * 5. Maintenance state — elevator will not move (handled by operator)
 * 6. Elevator states: UP, DOWN, IDLE, MAINTENANCE
 */
public class App {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("           Elevator System (LLD)              ");
        System.out.println("═══════════════════════════════════════════════\n");

        // ─── Create 3 elevators with DIFFERENT weight limits ────
        int minFloor = 0, maxFloor = 10;
        List<Elevator> elevators = Arrays.asList(
                new Elevator(1, minFloor, maxFloor, 700),   // 700 kg limit
                new Elevator(2, minFloor, maxFloor, 500),   // 500 kg limit
                new Elevator(3, minFloor, maxFloor, 800)    // 800 kg limit
        );

        ElevatorController controller = new ElevatorController(elevators, new LookAheadStrategy());
        Building building = new Building("Tech Park Tower", maxFloor, controller);

        // ═══════════════════════════════════════════════════════════
        //  SCENARIO 1: Basic hall call + inside floor button
        // ═══════════════════════════════════════════════════════════
        System.out.println("═══ Scenario 1: Hall call (outside UP button) + Floor button (inside) ═══");
        System.out.println("  Person on floor 5 presses UP (outside button → controls all carts)");
        building.callElevator(5, Direction.UP);
        // After pickup, passenger presses floor 8 (inside button → controls only Elevator-1)
        building.pressFloor(1, 8);
        building.simulate(9);

        // ═══════════════════════════════════════════════════════════
        //  SCENARIO 2: Open/Close door buttons inside elevator
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n\n═══ Scenario 2: Open/Close door buttons (inside) ═══");
        // Elevator-1 is at floor 8, someone presses open door
        building.pressOpenDoor(1);
        // Then presses close door
        building.pressCloseDoor(1);

        // ═══════════════════════════════════════════════════════════
        //  SCENARIO 3: Alarm button — that particular elevator stops
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n\n═══ Scenario 3: Alarm button pressed (inside Elevator-2) ═══");
        // First, make elevator 2 move
        building.callElevator(3, Direction.UP);
        building.simulate(1);
        // Now press alarm on Elevator-2
        building.pressAlarm(2);
        System.out.println("\n  Status after alarm:");
        controller.status();
        // Elevator-2 should not move even during simulation
        System.out.println("\n  Simulating 2 steps — Elevator-2 should NOT move:");
        building.simulate(2);
        // Operator resets alarm
        System.out.println("\n  Operator resets alarm on Elevator-2:");
        building.resetAlarm(2);
        controller.status();

        // ═══════════════════════════════════════════════════════════
        //  SCENARIO 4: Weight limit (overweight → stops, opens, alarm)
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n\n═══ Scenario 4: Overweight (Elevator-2 limit=500kg, load=600kg) ═══");
        building.updateWeight(2, 600);  // Exceeds 500kg limit
        System.out.println("\n  Status after overweight:");
        controller.status();
        // Fix: reduce weight
        System.out.println("\n  Passenger exits, weight now 400kg:");
        building.updateWeight(2, 400);
        controller.status();

        // ═══════════════════════════════════════════════════════════
        //  SCENARIO 5: Maintenance mode — elevator will not move
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n\n═══ Scenario 5: Maintenance mode (Elevator-3) ═══");
        building.setMaintenance(3);
        System.out.println("\n  All elevators down to 2, dispatching hall call:");
        building.callElevator(7, Direction.DOWN);
        // Elevator-3 should NOT be chosen
        controller.status();
        building.simulate(3);

        // Operator clears maintenance
        System.out.println("\n  Operator clears maintenance on Elevator-3:");
        building.clearMaintenance(3);
        controller.status();

        // ═══════════════════════════════════════════════════════════
        //  SCENARIO 6: Emergency button
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n\n═══ Scenario 6: Emergency button (inside Elevator-1) ═══");
        building.pressFloor(1, 5);  // Elevator-1 heading to floor 5
        building.simulate(1);
        // Press emergency mid-way
        building.pressEmergency(1);
        System.out.println("\n  Status after emergency:");
        controller.status();
        // Reset
        building.resetAlarm(1);

        // ═══════════════════════════════════════════════════════════
        //  SCENARIO 7: Multiple hall calls — system dispatches to
        //              different elevators
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n\n═══ Scenario 7: Multiple simultaneous hall calls ═══");
        building.callElevator(2, Direction.UP);
        building.callElevator(9, Direction.DOWN);
        building.callElevator(6, Direction.UP);
        building.simulate(10);

        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("            Simulation Complete!               ");
        System.out.println("═══════════════════════════════════════════════");
    }
}
