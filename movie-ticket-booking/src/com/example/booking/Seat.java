package com.example.booking;

public class Seat {
    private final int seatNumber;
    private final String row;
    private final SeatType type;

    public Seat(int seatNumber, String row, SeatType type) {
        this.seatNumber = seatNumber; this.row = row; this.type = type;
    }

    public int seatNumber() { return seatNumber; }
    public String row() { return row; }
    public SeatType type() { return type; }

    @Override
    public String toString() { return row + seatNumber + "(" + type + ")"; }
}
