# Elevator System

## Requirements

1. **Outside buttons** (UP, DOWN) — on every floor, controls **all** carts (dispatches one)
2. **Inside buttons:**
   - Floor button — controls only that **individual** cart
   - Open door, Close door buttons
   - Emergency button — stops elevator, opens door
3. **Alarm button** inside — that particular elevator **stops and rings alarm**
4. **Weight limit** per cart (e.g., 700kg) — variable, different for different carts
   - Overweight → elevator stops, opens door, alarm played
5. **Maintenance mode** — elevator will not move up and down (handled by operator)
6. **Elevator states:** UP, DOWN, IDLE (on a particular floor), MAINTENANCE

## Class Diagram

```
+------------------+        +-------------------+
|   <<enum>>       |        |    <<enum>>       |
|   Direction      |        |    DoorState      |
|------------------|        |-------------------|
| UP               |        | OPEN              |
| DOWN             |        | CLOSED            |
+------------------+        +-------------------+

+-------------------+        +-------------------+
|   <<enum>>        |        |   <<enum>>        |
|   ElevatorState   |        |   RequestType     |
|-------------------|        |-------------------|
| UP                |        | EXTERNAL          |
| DOWN              |        | INTERNAL          |
| IDLE              |        +-------------------+
| MAINTENANCE       |
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
|          Elevator              |
|-------------------------------|
| - id                          |
| - currentFloor                |
| - state: ElevatorState        |  ← UP, DOWN, IDLE, MAINTENANCE
| - doorState: DoorState        |
| - upStops: TreeSet<Int>       |
| - downStops: TreeSet<Int>     |
| - minFloor, maxFloor          |
| - maxWeightKg: double         |  ← variable per cart
| - currentWeightKg: double     |
| - alarmActive: boolean        |
|-------------------------------|
| + addStop(floor)              |  ← inside floor button
| + moveOneStep()               |
| + pressOpenDoor()             |  ← inside open button
| + pressCloseDoor()            |  ← inside close button
| + pressEmergency()            |  ← inside emergency button
| + pressAlarm()                |  ← inside alarm button
| + updateWeight(kg)            |  ← overweight detection
| + setMaintenance()            |  ← operator control
| + clearMaintenance()          |  ← operator control
| + resetAlarm()                |  ← operator control
| + isAvailable()               |
+-------------------------------+

+----------------------------+       +---------------------------+
| <<interface>>              |       | LookAheadStrategy         |
| ElevatorSelectionStrategy  |<|-----| (implements)              |
|----------------------------|       |---------------------------|
| + selectElevator(elevators,|       | Picks closest available   |
|     floor, direction)      |       | elevator (LOOK algorithm) |
+----------------------------+       +---------------------------+

+-------------------------------+
|      ElevatorController       |
|-------------------------------|
| - elevators: List<Elevator>   |
| - strategy                    |
| - pendingRequests: Queue      |
|-------------------------------|
| + requestElevator(floor, dir) |  ← outside button (all carts)
| + pressFloor(elevId, floor)   |  ← inside button (1 cart)
| + pressOpenDoor(elevId)       |  ← inside button (1 cart)
| + pressCloseDoor(elevId)      |  ← inside button (1 cart)
| + pressEmergency(elevId)      |  ← inside button (1 cart)
| + pressAlarm(elevId)          |  ← inside button (1 cart)
| + setMaintenance(elevId)      |  ← operator
| + clearMaintenance(elevId)    |  ← operator
| + resetAlarm(elevId)          |  ← operator
| + updateWeight(elevId, kg)    |
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
| + callElevator(floor, dir)    |  ← outside UP/DOWN button
| + pressFloor(elevId, floor)   |  ← inside floor button
| + pressOpenDoor(elevId)       |  ← inside open button
| + pressCloseDoor(elevId)      |  ← inside close button
| + pressEmergency(elevId)      |  ← inside emergency button
| + pressAlarm(elevId)          |  ← inside alarm button
| + updateWeight(elevId, kg)    |  ← weight sensor
| + setMaintenance(elevId)      |  ← operator
| + clearMaintenance(elevId)    |  ← operator
| + simulate(steps)             |
+-------------------------------+
```

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `ElevatorSelectionStrategy` | Pluggable elevator dispatch algorithm |
| **State** | `ElevatorState` enum | Elevator transitions: IDLE→UP/DOWN→IDLE, MAINTENANCE |
| **LOOK Algorithm** | `LookAheadStrategy` | Picks closest elevator in same direction, then idle, then least busy |

## Key Behaviors

| Feature | Behavior |
|---------|----------|
| **Outside UP/DOWN** | Controller dispatches ONE available cart from ALL carts |
| **Inside floor** | Only controls that individual cart |
| **Alarm** | That particular elevator stops and rings alarm |
| **Overweight** | Elevator stops, opens door, alarm plays until weight is reduced |
| **Maintenance** | Elevator will NOT move up/down; handled by operator |
| **Emergency** | Elevator stops immediately, opens door, alarm active |

## Build/Run

```
cd elevator-system/src
javac com/example/elevator/*.java
java com.example.elevator.App
```
