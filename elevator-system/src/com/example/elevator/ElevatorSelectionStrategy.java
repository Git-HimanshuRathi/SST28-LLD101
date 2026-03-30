package com.example.elevator;

import java.util.List;

public interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, int floor, Direction direction);
}
