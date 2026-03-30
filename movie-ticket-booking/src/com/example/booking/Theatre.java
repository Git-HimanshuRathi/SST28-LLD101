package com.example.booking;

import java.util.*;

public class Theatre {
    private final String name;
    private final List<Screen> screens;
    private final List<Show> shows = new ArrayList<>();
    private final BookingService bookingService;
    private int showCounter = 0;

    public Theatre(String name, List<Screen> screens, BookingService bookingService) {
        this.name = name;
        this.screens = Collections.unmodifiableList(new ArrayList<>(screens));
        this.bookingService = bookingService;
    }

    public Show addShow(Movie movie, Screen screen, long startTime) {
        String id = "SH-" + (++showCounter);
        Show show = new Show(id, movie, screen, startTime);
        shows.add(show);
        bookingService.registerShow(show);
        return show;
    }

    public String name() { return name; }
    public List<Screen> screens() { return screens; }
    public List<Show> shows() { return Collections.unmodifiableList(shows); }
    public BookingService bookingService() { return bookingService; }
}
