package com.example.booking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Core booking service handling seat booking, cancellation, and refunds.
 * Thread-safe: uses per-show locks for concurrent booking and a ConcurrentHashMap for bookings.
 * 
 * APIs:
 *   bookTickets(show_id, seats) -> MovieTicket (via Booking)
 *   cancelBooking(bookingId) -> refund processed
 */
public class BookingService {
    private final Map<String, List<ShowSeat>> showSeatMap = new ConcurrentHashMap<>();
    private final DefaultPricingPolicy pricingPolicy;
    private final SeatSelectionStrategy selectionStrategy;
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final RefundPolicy refundPolicy;

    /** Per-show lock for handling concurrent seat booking. */
    private final Map<String, ReentrantLock> showLocks = new ConcurrentHashMap<>();
    private int bookingCounter = 0;

    public BookingService(DefaultPricingPolicy pricingPolicy,
                          SeatSelectionStrategy selectionStrategy,
                          RefundPolicy refundPolicy) {
        this.pricingPolicy = pricingPolicy;
        this.selectionStrategy = selectionStrategy;
        this.refundPolicy = refundPolicy;
    }

    /** Register all seats for a show (called when a show is added to the theatre). */
    public void registerShow(Show show) {
        List<ShowSeat> showSeats = show.screen().seats().stream()
                .map(seat -> new ShowSeat(seat, show))
                .collect(Collectors.toList());
        showSeatMap.put(show.showId(), showSeats);
        showLocks.put(show.showId(), new ReentrantLock());
    }

    /** Get available (unbooked) seats for a show, optionally filtered by type. */
    public List<ShowSeat> getAvailableSeats(Show show, SeatType type) {
        List<ShowSeat> all = showSeatMap.getOrDefault(show.showId(), Collections.emptyList());
        return all.stream()
                .filter(ss -> !ss.isBooked())
                .filter(ss -> type == null || ss.seat().type() == type)
                .collect(Collectors.toList());
    }

    /**
     * Book tickets: bookTickets(show_id, seats) → MovieTicket (per requirement).
     * Uses per-show lock to handle concurrency — only one booking can proceed
     * for the same show at a time, preventing double-booking.
     *
     * @param user     the user booking
     * @param show     the show (contains show_id)
     * @param seatType desired seat type
     * @param count    number of seats to book
     * @return Booking containing MovieTickets, or null if unavailable
     */
    public Booking bookTickets(User user, Show show, SeatType seatType, int count) {
        ReentrantLock lock = showLocks.get(show.showId());
        if (lock == null) throw new IllegalStateException("Show not registered: " + show.showId());

        lock.lock();
        try {
            List<ShowSeat> available = getAvailableSeats(show, null);
            List<ShowSeat> selected = selectionStrategy.selectSeats(available, count, seatType);

            if (selected.isEmpty()) {
                System.out.println("  ✗ No " + count + " " + seatType + " seats available for " + show);
                return null;
            }

            // Book the seats
            for (ShowSeat ss : selected) ss.book();

            // Calculate price using base + dynamic pricing rules
            double total = 0;
            List<MovieTicket> tickets = new ArrayList<>();
            for (ShowSeat ss : selected) {
                double price = pricingPolicy.getFinalPrice(show, ss.seat().type());
                total += price;
            }

            String id = "BK-" + (++bookingCounter);
            Booking booking = new Booking(id, show, selected, user, total);
            bookings.put(id, booking);

            // Create MovieTicket objects
            for (ShowSeat ss : selected) {
                double price = pricingPolicy.getFinalPrice(show, ss.seat().type());
                tickets.add(new MovieTicket(booking, ss, price));
            }
            booking.setTickets(tickets);

            System.out.println("  ✔ " + booking);
            return booking;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Cancel a booking, release seats, and process refund.
     * Thread-safe using per-show lock.
     */
    public double cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) throw new IllegalArgumentException("Unknown booking: " + bookingId);

        ReentrantLock lock = showLocks.get(booking.show().showId());
        lock.lock();
        try {
            double refundAmount = refundPolicy.calculateRefund(booking);
            booking.cancel();
            System.out.println("  ✔ Cancelled: " + booking + " | Refund: ₹" + String.format("%.2f", refundAmount));
            return refundAmount;
        } finally {
            lock.unlock();
        }
    }
}
