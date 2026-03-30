package com.example.booking;

/**
 * Represents a single movie ticket within a booking.
 * Returned as part of the bookTickets API response.
 */
public class MovieTicket {
    private final Booking booking;
    private final ShowSeat showSeat;
    private final double price;

    public MovieTicket(Booking booking, ShowSeat showSeat, double price) {
        this.booking = booking;
        this.showSeat = showSeat;
        this.price = price;
    }

    public Booking booking() { return booking; }
    public ShowSeat showSeat() { return showSeat; }
    public double price() { return price; }

    @Override
    public String toString() {
        return "Ticket{" + showSeat.seat() + ", movie=" + booking.show().movie().title()
                + ", price=" + String.format("%.2f", price) + "}";
    }
}
