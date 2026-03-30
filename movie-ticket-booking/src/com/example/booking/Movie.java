package com.example.booking;

public class Movie {
    private final String movieId;
    private final String title;
    private final int durationMinutes;

    public Movie(String movieId, String title, int durationMinutes) {
        this.movieId = movieId; this.title = title; this.durationMinutes = durationMinutes;
    }

    public String movieId() { return movieId; }
    public String title() { return title; }
    public int durationMinutes() { return durationMinutes; }

    @Override
    public String toString() { return title + " (" + durationMinutes + "min)"; }
}
