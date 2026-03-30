# Movie Ticket Booking System

## Requirements

1. **APIs:**
   - `showTheatres(userCity)` → `List<Theatre>`
   - `showMovies(city)` → `List<Movie>` in theatres in the city
   - `bookTickets(show_id, seats)` → `MovieTicket`
2. **User Flows:**
   - City → Movies → Theatres → Show → Book seats
   - City → Theatres → Movies → Show → Book seats
3. **Seat Types:** SILVER, GOLD, PLATINUM with fixed base price per seat/theatre/movie
4. **Dynamic Pricing:** Base price + rules (weekend surcharge, peak-hour surcharge, etc.)
5. **Cancellation:** Refund must be processed (full/partial based on time)
6. **Concurrency:** Thread-safe seat booking + admin show addition

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

+-------------------+       +-------------------+
|      User         |       |      City         |
|-------------------|       |-------------------|
| - userId          |       | - cityId          |
| - name            |       | - name            |
| - email           |       | - theatres: List  |
+-------------------+       +-------------------+

+---------------------+
|        Seat          |
|---------------------|
| - seatNumber        |
| - row               |
| - type: SeatType    |
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

+----------------------------+
|     MovieTicket             |
|----------------------------|
| - booking: Booking         |
| - showSeat: ShowSeat       |
| - price: double            |
+----------------------------+

+-------------------------+
|     Booking             |
|-------------------------|
| - bookingId             |
| - show: Show            |
| - seats: List<ShowSeat> |
| - tickets: List<Ticket> |
| - user: User            |
| - status                |
| - totalAmount           |
|-------------------------|
| + cancel()              |
+-------------------------+

+---------------------------+       +---------------------------+
| <<interface>>             |       | <<interface>>             |
| SeatSelectionStrategy     |       | PricingPolicy             |
|---------------------------|       |---------------------------|
| + selectSeats(available,  |       | + getPrice(seatType)      |
|       count, type)        |       +---------------------------+
+---------------------------+                ^
         ^                                   |
         |                       +--------------------------+
+---------------------------+   | DefaultPricingPolicy      |
| ConsecutiveSeatStrategy   |   |--------------------------|
|---------------------------|   | - basePrices: Map         |
| + selectSeats()           |   | - rules: List<PricingRule>|
+---------------------------+   | + getFinalPrice(show,type)|
                                | + addRule(rule)           |
                                +--------------------------+

+---------------------------+
| <<interface>>             |
| PricingRule               |      (Strategy Pattern)
|---------------------------|
| + apply(show, type, price)|
+---------------------------+
         ^
         |
    +----+------------------+
    |                       |
+-------------------+  +---------------------+
| WeekendPricingRule|  | PeakHourPricingRule  |
|-------------------|  |---------------------|
| - surchargePercent|  | - peakStartHour     |
+-------------------+  | - peakEndHour       |
                       | - surchargeAmount   |
                       +---------------------+

+---------------------------+       +--------------------------+
| <<interface>>             |       | DefaultRefundPolicy      |
| RefundPolicy              |<|-----| (implements)             |
|---------------------------|       |--------------------------|
| + calculateRefund(booking)|       | 100% if >2hrs before show|
+---------------------------+       | 50% otherwise            |
                                    +--------------------------+

+-------------------------------+
|      BookingService            |       (Thread-safe: per-show locks)
|-------------------------------|
| - showSeatMap: ConcurrentMap  |
| - pricingPolicy               |
| - selectionStrategy           |
| - bookings: ConcurrentMap     |
| - showLocks: Map<ReentrantLock>|
| - refundPolicy                |
|-------------------------------|
| + registerShow(show)          |
| + bookTickets(user, show,     |  ← synchronized per show
|     seatType, count)          |
| + cancelBooking(bookingId)    |  ← refund processed
| + getAvailableSeats(show,type)|
+-------------------------------+

+-----------------------------+
|        Theatre              |       (Concurrent admin show addition)
|-----------------------------|
| - theatreId                 |
| - name                      |
| - city: String              |
| - screens: List<Screen>     |
| - shows: List<Show>         |
| - bookingService            |
| - showLock: ReentrantLock   |
|-----------------------------|
| + addShow(movie, screen,    |  ← synchronized, validates overlaps
|           startTime)        |
| + getMovies()               |
| + getShowsForMovie(movie)   |
+-----------------------------+

+-----------------------------------+
| MovieTicketBookingService          |    (Platform-level service)
|-----------------------------------|
| - cities: Map<String, City>       |
|-----------------------------------|
| + showTheatres(cityName)          |  ← API
| + showMovies(cityName)            |  ← API
| + getTheatresForMovie(city, movie)|
| + getMoviesInTheatre(theatre)     |
| + getShowsInTheatre(theatre, mov) |
+-----------------------------------+
```

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `PricingRule`, `SeatSelectionStrategy`, `RefundPolicy` | Plug different pricing rules, seat selection, refund policies |
| **Composition** | `DefaultPricingPolicy` + `List<PricingRule>` | Multiple dynamic pricing rules composed on base price |
| **Concurrency** | `ReentrantLock` per show in `BookingService`, per theatre in `Theatre` | Thread-safe booking + admin show addition |

## User Flows

### Flow 1: City → Movies → Theatre → Book
```
User → showMovies("Mumbai") → picks "Inception"
     → getTheatresForMovie("Mumbai", inception) → picks "PVR"
     → getShowsForMovieAtTheatre(pvr, inception) → picks show
     → bookTickets(user, show, GOLD, 3) → MovieTicket
```

### Flow 2: City → Theatres → Movie → Book
```
User → showTheatres("Mumbai") → picks "INOX"
     → getMoviesInTheatre(inox) → picks "Oppenheimer"
     → getShowsInTheatre(inox, oppenheimer) → picks show
     → bookTickets(user, show, PLATINUM, 2) → MovieTicket
```

## Build/Run

```
cd movie-ticket-booking/src
javac com/example/booking/*.java
java com.example.booking.App
```
