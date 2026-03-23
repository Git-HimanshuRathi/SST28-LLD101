# Multilevel Parking Lot

## Class Diagram

```
+------------------+        +-------------------+
|   <<enum>>       |        |    <<enum>>       |
|   VehicleType    |        |    SlotType       |
|------------------|        |-------------------|
| TWO_WHEELER      |        | SMALL             |
| CAR              |        | MEDIUM            |
| BUS              |        | LARGE             |
+------------------+        +-------------------+

+-------------------+       +-------------------+
|     Vehicle       |       |      Gate         |
|-------------------|       |-------------------|
| - licensePlate    |       | - gateNumber      |
| - type            |       | - floor           |
+-------------------+       +-------------------+

+---------------------+
|    ParkingSlot       |
|---------------------|
| - slotNumber        |
| - type: SlotType    |
| - floor             |
| - occupied          |
|---------------------|
| + occupy()          |
| + free()            |
+---------------------+

+-------------------------+
|     ParkingTicket       |
|-------------------------|
| - ticketId              |
| - vehicle: Vehicle      |
| - slot: ParkingSlot     |
| - entryTime             |
+-------------------------+

+-------------------+
|       Bill        |
|-------------------|
| - ticket          |
| - exitTime        |
| - hoursParked     |
| - totalAmount     |
+-------------------+

+---------------------------+
| VehicleSlotCompatibility  |
|---------------------------|
| + compatibleSlots()       |
+---------------------------+

+-----------------------------+       +-------------------------+
| <<interface>>               |       |  NearestSlotStrategy    |
| SlotAssignmentStrategy      |<|-----|  (implements)           |
|-----------------------------|       |-------------------------|
| + findSlot(slots, type,     |       | + findSlot()            |
|            gate)            |       +-------------------------+
+-----------------------------+

+-----------------------------+       +-------------------------+
| <<interface>>               |       |  DefaultPricingPolicy   |
| PricingPolicy               |<|-----|  (implements)           |
|-----------------------------|       |-------------------------|
| + ratePerHour(slotType)     |       | - rates: Map            |
+-----------------------------+       | + ratePerHour()         |
                                      +-------------------------+

+---------------------+
|   BillingService    |
|---------------------|
| - pricingPolicy     |
|---------------------|
| + generateBill()    |
+---------------------+

+-----------------------------+
|        ParkingLot           |
|-----------------------------|
| - slots: List<ParkingSlot>  |
| - assignmentStrategy        |
| - billingService            |
| - activeTickets: Map        |
|-----------------------------|
| + entry(vehicle, gate, time)|
| + exit(ticketId, time)      |
+-----------------------------+
```
