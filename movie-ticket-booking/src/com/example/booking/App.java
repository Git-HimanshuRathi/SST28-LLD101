package com.example.booking;

import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Movie Ticket Booking System ===\n");

        // --- Build screen with seats ---
        List<Seat> audi1Seats = new ArrayList<>();
        // Row A: Silver (1-5)
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "A", SeatType.SILVER));
        // Row B: Silver (1-5)
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "B", SeatType.SILVER));
        // Row C: Gold (1-5)
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "C", SeatType.GOLD));
        // Row D: Gold (1-5)
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "D", SeatType.GOLD));
        // Row E: Platinum (1-5)
        for (int i = 1; i <= 5; i++) audi1Seats.add(new Seat(i, "E", SeatType.PLATINUM));

        Screen screen1 = new Screen("SCR-1", "Audi 1", audi1Seats);

        // --- Build theatre ---
        BookingService bookingService = new BookingService(new DefaultPricingPolicy(), new ConsecutiveSeatStrategy());
        Theatre theatre = new Theatre("PVR Cinemas", Arrays.asList(screen1), bookingService);

        // --- Add shows ---
        Movie movie1 = new Movie("MOV-1", "Inception", 148);
        Movie movie2 = new Movie("MOV-2", "Interstellar", 169);
        long now = System.currentTimeMillis();

        Show show1 = theatre.addShow(movie1, screen1, now);
        Show show2 = theatre.addShow(movie2, screen1, now + 10800000); // 3 hours later

        System.out.println("Shows:");
        for (Show s : theatre.shows()) System.out.println("  " + s.showId() + " -> " + s);

        // --- Users ---
        User alice = new User("U-1", "Alice", "alice@example.com");
        User bob   = new User("U-2", "Bob", "bob@example.com");
        User carol = new User("U-3", "Carol", "carol@example.com");

        // --- Book tickets ---
        System.out.println("\n--- Booking tickets for " + show1 + " ---");
        Booking b1 = bookingService.bookTickets(alice, show1, SeatType.GOLD, 3);
        Booking b2 = bookingService.bookTickets(bob, show1, SeatType.SILVER, 2);
        Booking b3 = bookingService.bookTickets(carol, show1, SeatType.PLATINUM, 2);

        // --- Show available seats ---
        System.out.println("\n--- Available GOLD seats for " + show1 + " ---");
        List<ShowSeat> availGold = bookingService.getAvailableSeats(show1, SeatType.GOLD);
        for (ShowSeat ss : availGold) System.out.println("  " + ss);

        // --- Cancel a booking ---
        System.out.println("\n--- Cancelling booking " + b1.bookingId() + " ---");
        bookingService.cancelBooking(b1.bookingId());

        // --- Show available GOLD seats after cancellation ---
        System.out.println("\n--- Available GOLD seats after cancellation ---");
        availGold = bookingService.getAvailableSeats(show1, SeatType.GOLD);
        for (ShowSeat ss : availGold) System.out.println("  " + ss);

        // --- Book for show2 ---
        System.out.println("\n--- Booking tickets for " + show2 + " ---");
        bookingService.bookTickets(alice, show2, SeatType.PLATINUM, 5);
        bookingService.bookTickets(bob, show2, SeatType.PLATINUM, 1); // should fail – only 5 platinum seats
    }
}
