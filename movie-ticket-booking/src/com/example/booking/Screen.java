package com.example.booking;

import java.util.*;

public class Screen {
    private final String screenId;
    private final String name;
    private final List<Seat> seats;

    public Screen(String screenId, String name, List<Seat> seats) {
        this.screenId = screenId; this.name = name;
        this.seats = Collections.unmodifiableList(new ArrayList<>(seats));
    }

    public String screenId() { return screenId; }
    public String name() { return name; }
    public List<Seat> seats() { return seats; }

    @Override
    public String toString() { return name + " (" + seats.size() + " seats)"; }
}
