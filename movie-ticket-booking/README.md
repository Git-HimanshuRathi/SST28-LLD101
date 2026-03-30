# Movie Ticket Booking System

## Class Diagram

```
+------------------+        +-------------------+
|   <<enum>>       |        |    <<enum>>       |
|   SeatType       |        |    BookingStatus  |
|------------------|        |-------------------|
| SILVER           |        | CONFIRMED         |
| GOLD             |        | CANCELLED         |
| PLATINUM         |        +-------------------+
+------------------+

+-------------------+       +-------------------+
|      Movie        |       |      Screen       |
|-------------------|       |-------------------|
| - movieId         |       | - screenId        |
| - title           |       | - name            |
| - durationMinutes |       | - seats: List     |
+-------------------+       +-------------------+

+---------------------+
|        Seat          |
|---------------------|
| - seatNumber        |
| - row               |
| - type: SeatType    |
|---------------------|
| + toString()        |
+---------------------+

+---------------------+
|        Show          |
|---------------------|
| - showId            |
| - movie: Movie      |
| - screen: Screen    |
| - startTime         |
+---------------------+

+----------------------------+
|       ShowSeat              |
|----------------------------|
| - seat: Seat               |
| - show: Show               |
| - booked: boolean          |
|----------------------------|
| + book()                   |
| + release()                |
+----------------------------+

+-------------------------+
|     Booking             |
|-------------------------|
| - bookingId             |
| - show: Show            |
| - seats: List<ShowSeat> |
| - user: User            |
| - status                |
| - totalAmount           |
|-------------------------|
| + cancel()              |
+-------------------------+

+-------------------+
|      User         |
|-------------------|
| - userId          |
| - name            |
| - email           |
+-------------------+

+---------------------------+
| <<interface>>             |
| SeatSelectionStrategy     |
|---------------------------|
| + selectSeats(available,  |
|       count, type)        |
+---------------------------+
         ^
         |
+---------------------------+
| ConsecutiveSeatStrategy   |
|---------------------------|
| + selectSeats()           |
+---------------------------+

+---------------------------+       +-------------------------+
| <<interface>>             |       | DefaultPricingPolicy    |
| PricingPolicy             |<|-----| (implements)            |
|---------------------------|       |-------------------------|
| + getPrice(seatType)      |       | - prices: Map           |
+---------------------------+       | + getPrice()            |
                                    +-------------------------+

+-----------------------------+
|      BookingService         |
|-----------------------------|
| - showSeatMap: Map          |
| - pricingPolicy             |
| - selectionStrategy         |
| - bookings: Map             |
|-----------------------------|
| + bookTickets(user, show,   |
|     seatType, count)        |
| + cancelBooking(bookingId)  |
| + getAvailableSeats(show,   |
|     seatType)               |
+-----------------------------+

+-----------------------------+
|        Theatre              |
|-----------------------------|
| - name                      |
| - screens: List<Screen>     |
| - shows: List<Show>         |
| - bookingService            |
|-----------------------------|
| + addShow(movie, screen,    |
|           startTime)        |
+-----------------------------+
```

## Build/Run

```
cd movie-ticket-booking/src
javac com/example/booking/*.java
java com.example.booking.App
```
