package com.example.booking;

import java.util.*;

/**
 * Represents a confirmed booking containing seats and tickets.
 * Supports cancellation to release seats.
 */
public class Booking {
    private final String bookingId;
    private final Show show;
    private final List<ShowSeat> seats;
    private final User user;
    private final double totalAmount;
    private BookingStatus status;
    private List<MovieTicket> tickets;

    public Booking(String bookingId, Show show, List<ShowSeat> seats,
                   User user, double totalAmount) {
        this.bookingId = bookingId;
        this.show = show;
        this.seats = Collections.unmodifiableList(new ArrayList<>(seats));
        this.user = user;
        this.totalAmount = totalAmount;
        this.status = BookingStatus.CONFIRMED;
    }

    public String bookingId() { return bookingId; }
    public Show show() { return show; }
    public List<ShowSeat> seats() { return seats; }
    public User user() { return user; }
    public double totalAmount() { return totalAmount; }
    public BookingStatus status() { return status; }
    public List<MovieTicket> tickets() { return tickets; }

    public void setTickets(List<MovieTicket> tickets) {
        this.tickets = Collections.unmodifiableList(new ArrayList<>(tickets));
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking already cancelled");
        }
        for (ShowSeat ss : seats) ss.release();
        status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Booking{id=" + bookingId + ", movie=" + show.movie().title()
                + ", seats=" + seats.size() + ", user=" + user.name()
                + ", amount=₹" + String.format("%.2f", totalAmount)
                + ", status=" + status + "}";
    }
}
