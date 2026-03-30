package com.example.booking;

import java.util.*;
import java.util.stream.Collectors;

public class BookingService {
    private final Map<String, List<ShowSeat>> showSeatMap = new HashMap<>();
    private final PricingPolicy pricingPolicy;
    private final SeatSelectionStrategy selectionStrategy;
    private final Map<String, Booking> bookings = new HashMap<>();
    private int bookingCounter = 0;

    public BookingService(PricingPolicy pricingPolicy, SeatSelectionStrategy selectionStrategy) {
        this.pricingPolicy = pricingPolicy; this.selectionStrategy = selectionStrategy;
    }

    /** Register all seats for a show (called when a show is added to the theatre). */
    public void registerShow(Show show) {
        List<ShowSeat> showSeats = show.screen().seats().stream()
                .map(seat -> new ShowSeat(seat, show))
                .collect(Collectors.toList());
        showSeatMap.put(show.showId(), showSeats);
    }

    /** Get available (unbooked) seats for a show, optionally filtered by type. */
    public List<ShowSeat> getAvailableSeats(Show show, SeatType type) {
        List<ShowSeat> all = showSeatMap.getOrDefault(show.showId(), Collections.emptyList());
        return all.stream()
                .filter(ss -> !ss.isBooked())
                .filter(ss -> type == null || ss.seat().type() == type)
                .collect(Collectors.toList());
    }

    /** Book tickets for a user. */
    public Booking bookTickets(User user, Show show, SeatType seatType, int count) {
        List<ShowSeat> available = getAvailableSeats(show, null);
        List<ShowSeat> selected = selectionStrategy.selectSeats(available, count, seatType);

        if (selected.isEmpty()) {
            System.out.println("  ✗ No " + count + " " + seatType + " seats available for " + show);
            return null;
        }

        for (ShowSeat ss : selected) ss.book();

        double total = selected.stream()
                .mapToDouble(ss -> pricingPolicy.getPrice(ss.seat().type()))
                .sum();

        String id = "BK-" + (++bookingCounter);
        Booking booking = new Booking(id, show, selected, user, total);
        bookings.put(id, booking);

        System.out.println("  ✔ " + booking);
        return booking;
    }

    /** Cancel an existing booking, releasing the seats. */
    public void cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) throw new IllegalArgumentException("Unknown booking: " + bookingId);
        booking.cancel();
        System.out.println("  ✔ Cancelled: " + booking);
    }
}
