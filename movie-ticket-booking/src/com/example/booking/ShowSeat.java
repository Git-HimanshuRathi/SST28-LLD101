package com.example.booking;

public class ShowSeat {
    private final Seat seat;
    private final Show show;
    private boolean booked;

    public ShowSeat(Seat seat, Show show) {
        this.seat = seat; this.show = show;
    }

    public Seat seat() { return seat; }
    public Show show() { return show; }
    public boolean isBooked() { return booked; }

    public void book() {
        if (booked) throw new IllegalStateException("Seat " + seat + " is already booked");
        booked = true;
    }

    public void release() {
        if (!booked) throw new IllegalStateException("Seat " + seat + " is not booked");
        booked = false;
    }

    @Override
    public String toString() { return seat.toString() + (booked ? " [BOOKED]" : " [AVAILABLE]"); }
}
