package com.example.booking;

public class Show {
    private final String showId;
    private final Movie movie;
    private final Screen screen;
    private final long startTime;

    public Show(String showId, Movie movie, Screen screen, long startTime) {
        this.showId = showId; this.movie = movie; this.screen = screen; this.startTime = startTime;
    }

    public String showId() { return showId; }
    public Movie movie() { return movie; }
    public Screen screen() { return screen; }
    public long startTime() { return startTime; }

    @Override
    public String toString() { return movie.title() + " @ " + screen.name(); }
}
