package com.example.booking;

import java.util.*;

/**
 * Demo application showing the full Movie Ticket Booking System:
 *
 * 1. City → Theatres / Movies browsing (both user flows)
 * 2. bookTickets(show_id, seats) → MovieTicket
 * 3. Cancellation with refund
 * 4. Dynamic pricing rules (weekend, peak-hour)
 * 5. Concurrency for booking + admin show addition
 */
public class App {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("       Movie Ticket Booking System (LLD)      ");
        System.out.println("═══════════════════════════════════════════════\n");

        // ─── 1. Setup pricing with rules ────────────────────────
        DefaultPricingPolicy pricingPolicy = new DefaultPricingPolicy();
        pricingPolicy.addRule(new WeekendPricingRule(20));            // +20% on weekends
        pricingPolicy.addRule(new PeakHourPricingRule(18, 22, 50));   // +₹50 for 6-10 PM

        // ─── 2. Build screens + seats ───────────────────────────
        List<Seat> audi1Seats = new ArrayList<>();
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "A", SeatType.SILVER));
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "B", SeatType.SILVER));
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "C", SeatType.GOLD));
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "D", SeatType.GOLD));
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "E", SeatType.PLATINUM));
        Screen screen1 = new Screen("SCR-1", "Audi 1", audi1Seats);

        List<Seat> audi2Seats = new ArrayList<>();
        for (int i = 1; i <= 4; i++) audi2Seats.add(new Seat(i, "A", SeatType.SILVER));
        for (int i = 1; i <= 4; i++) audi2Seats.add(new Seat(i, "B", SeatType.GOLD));
        for (int i = 1; i <= 4; i++) audi2Seats.add(new Seat(i, "C", SeatType.PLATINUM));
        Screen screen2 = new Screen("SCR-2", "Audi 2", audi2Seats);

        // ─── 3. Build theatres with BookingService ──────────────
        RefundPolicy refundPolicy = new DefaultRefundPolicy();
        BookingService bs1 = new BookingService(pricingPolicy, new ConsecutiveSeatStrategy(), refundPolicy);
        BookingService bs2 = new BookingService(pricingPolicy, new ConsecutiveSeatStrategy(), refundPolicy);

        Theatre pvr = new Theatre("TH-1", "PVR Cinemas", "Mumbai",
                Arrays.asList(screen1), bs1);
        Theatre inox = new Theatre("TH-2", "INOX Megaplex", "Mumbai",
                Arrays.asList(screen2), bs2);

        // ─── 4. Build city + platform ───────────────────────────
        City mumbai = new City("CITY-1", "Mumbai");
        mumbai.addTheatre(pvr);
        mumbai.addTheatre(inox);

        MovieTicketBookingService platform = new MovieTicketBookingService();
        platform.addCity(mumbai);

        // ─── 5. Add movies & shows (admin) ──────────────────────
        Movie inception = new Movie("MOV-1", "Inception", 148);
        Movie interstellar = new Movie("MOV-2", "Interstellar", 169);
        Movie oppenheimer = new Movie("MOV-3", "Oppenheimer", 180);

        long now = System.currentTimeMillis();
        Show show1 = pvr.addShow(inception, screen1, now);
        Show show2 = pvr.addShow(interstellar, screen1, now + 10800000);  // 3 hrs later
        Show show3 = inox.addShow(inception, screen2, now);
        Show show4 = inox.addShow(oppenheimer, screen2, now + 12600000);  // 3.5 hrs later

        // ═══════════════════════════════════════════════════════════
        //  USER FLOW 1: Search by city → Movies → Theatres → Book
        // ═══════════════════════════════════════════════════════════
        System.out.println("═══ USER FLOW 1: City → Movies → Theatre → Book ═══\n");

        // Step 1: showMovies(city)
        List<Movie> moviesInMumbai = platform.showMovies("Mumbai");
        System.out.println("Movies in Mumbai:");
        for (Movie m : moviesInMumbai) System.out.println("  • " + m);

        // Step 2: User picks "Inception" → get theatres showing it
        List<Theatre> theatresForInception = platform.getTheatresForMovie("Mumbai", inception);
        System.out.println("\nTheatres showing 'Inception' in Mumbai:");
        for (Theatre t : theatresForInception) System.out.println("  • " + t.name());

        // Step 3: User picks PVR → get shows
        List<Show> showsAtPvr = platform.getShowsForMovieAtTheatre(pvr, inception);
        System.out.println("\nShows for 'Inception' at PVR:");
        for (Show s : showsAtPvr) System.out.println("  • " + s.showId() + " → " + s);

        // Step 4: Book seats
        User alice = new User("U-1", "Alice", "alice@example.com");
        System.out.println("\n--- Alice books 3 GOLD seats for " + show1 + " ---");
        Booking b1 = bs1.bookTickets(alice, show1, SeatType.GOLD, 3);
        if (b1 != null && b1.tickets() != null) {
            System.out.println("  Tickets issued:");
            for (MovieTicket t : b1.tickets()) System.out.println("    " + t);
        }

        // ═══════════════════════════════════════════════════════════
        //  USER FLOW 2: Search by city → Theatres → Movies → Book
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n═══ USER FLOW 2: City → Theatres → Movie → Book ═══\n");

        // Step 1: showTheatres(city)
        List<Theatre> theatresInMumbai = platform.showTheatres("Mumbai");
        System.out.println("Theatres in Mumbai:");
        for (Theatre t : theatresInMumbai) System.out.println("  • " + t.name());

        // Step 2: User picks INOX → get movies
        List<Movie> moviesAtInox = platform.getMoviesInTheatre(inox);
        System.out.println("\nMovies at INOX Megaplex:");
        for (Movie m : moviesAtInox) System.out.println("  • " + m);

        // Step 3: User picks Oppenheimer → shows
        List<Show> oppShows = platform.getShowsInTheatre(inox, oppenheimer);
        System.out.println("\nShows for 'Oppenheimer' at INOX:");
        for (Show s : oppShows) System.out.println("  • " + s.showId() + " → " + s);

        // Step 4: Book
        User bob = new User("U-2", "Bob", "bob@example.com");
        System.out.println("\n--- Bob books 2 PLATINUM seats for " + show4 + " ---");
        Booking b2 = bs2.bookTickets(bob, show4, SeatType.PLATINUM, 2);

        // ═══════════════════════════════════════════════════════════
        //  CANCELLATION WITH REFUND
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n═══ CANCELLATION & REFUND ═══\n");

        System.out.println("--- Cancelling Alice's booking: " + b1.bookingId() + " ---");
        double refund = bs1.cancelBooking(b1.bookingId());

        // Show available GOLD seats released
        System.out.println("\nAvailable GOLD seats after cancellation:");
        List<ShowSeat> availGold = bs1.getAvailableSeats(show1, SeatType.GOLD);
        for (ShowSeat ss : availGold) System.out.println("  " + ss);

        // ═══════════════════════════════════════════════════════════
        //  CONCURRENCY DEMO: Concurrent booking + admin show addition
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n═══ CONCURRENCY DEMO ═══\n");

        User carol = new User("U-3", "Carol", "carol@example.com");
        User dave = new User("U-4", "Dave", "dave@example.com");

        Thread t1 = new Thread(() -> {
            System.out.println("[Thread-1] Carol booking 5 SILVER for " + show1);
            bs1.bookTickets(carol, show1, SeatType.SILVER, 5);
        });
        Thread t2 = new Thread(() -> {
            System.out.println("[Thread-2] Dave booking 5 SILVER for " + show1);
            bs1.bookTickets(dave, show1, SeatType.SILVER, 5);
        });
        // Admin concurrently adding a new show
        Thread t3 = new Thread(() -> {
            try {
                System.out.println("[Thread-3] Admin adding new show...");
                Movie dark = new Movie("MOV-4", "The Dark Knight", 152);
                pvr.addShow(dark, screen1, now + 25200000); // 7 hrs later
                System.out.println("[Thread-3] ✔ Show added successfully");
            } catch (IllegalStateException e) {
                System.out.println("[Thread-3] ✗ " + e.getMessage());
            }
        });

        t1.start(); t2.start(); t3.start();
        try { t1.join(); t2.join(); t3.join(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Only 10 SILVER seats total → one thread should fail
        System.out.println("\nRemaining SILVER seats for " + show1 + ":");
        List<ShowSeat> remainingSilver = bs1.getAvailableSeats(show1, SeatType.SILVER);
        System.out.println("  Available: " + remainingSilver.size());

        // ═══════════════════════════════════════════════════════════
        //  ADMIN: Overlapping show validation
        // ═══════════════════════════════════════════════════════════
        System.out.println("\n═══ ADMIN: Overlapping show validation ═══\n");
        try {
            pvr.addShow(inception, screen1, now + 60000); // 1 min after existing show
            System.out.println("  ✗ Should have thrown!");
        } catch (IllegalStateException e) {
            System.out.println("  ✔ Correctly rejected: " + e.getMessage());
        }

        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("                  Done!                        ");
        System.out.println("═══════════════════════════════════════════════");
    }
}
