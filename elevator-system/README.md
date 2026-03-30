# Elevator System

## Class Diagram

```
+------------------+        +-------------------+
|   <<enum>>       |        |    <<enum>>       |
|   Direction      |        |    DoorState      |
|------------------|        |-------------------|
| UP               |        | OPEN              |
| DOWN             |        | CLOSED            |
| IDLE             |        +-------------------+
+------------------+

+-------------------+
|   <<enum>>        |
|   RequestType     |
|-------------------|
| EXTERNAL          |
| INTERNAL          |
+-------------------+

+-------------------+
|      Request      |
|-------------------|
| - floor           |
| - direction       |
| - type            |
| - timestamp       |
+-------------------+

+-------------------------------+
|          Elevator             |
|-------------------------------|
| - id                          |
| - currentFloor                |
| - direction: Direction        |
| - doorState: DoorState        |
| - upStops: TreeSet<Int>       |
| - downStops: TreeSet<Int>     |
| - minFloor                    |
| - maxFloor                    |
|-------------------------------|
| + addStop(floor)              |
| + moveOneStep()               |
| + openDoor()                  |
| + closeDoor()                 |
| + hasStops()                  |
| + stopsCount()                |
+-------------------------------+

+----------------------------+       +---------------------------+
| <<interface>>              |       | LookAheadStrategy         |
| ElevatorSelectionStrategy  |<|-----| (implements)              |
|----------------------------|       |---------------------------|
| + selectElevator(elevators,|       | + selectElevator()        |
|     floor, direction)      |       +---------------------------+
+----------------------------+

+-------------------------------+
|      ElevatorController       |
|-------------------------------|
| - elevators: List<Elevator>   |
| - strategy                    |
| - pendingRequests: Queue      |
|-------------------------------|
| + requestElevator(floor, dir) |
| + pressFloor(elevatorId,      |
|              floor)           |
| + step()                      |
| + status()                    |
+-------------------------------+

+-------------------------------+
|         Building              |
|-------------------------------|
| - name                        |
| - totalFloors                 |
| - controller                  |
|-------------------------------|
| + callElevator(floor, dir)    |
| + pressFloor(elevId, floor)   |
| + simulate(steps)             |
+-------------------------------+
```

## Design Patterns Used
- **Strategy Pattern** — `ElevatorSelectionStrategy` for pluggable elevator dispatch
- **State tracking** — Each elevator tracks its own direction, door state, and stop sets
- **LOOK algorithm** — `LookAheadStrategy` picks the closest elevator going in the same direction

## Build/Run

```
cd elevator-system/src
javac com/example/elevator/*.java
java com.example.elevator.App
```
