package com.example.booking;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * A theatre in a city with multiple screens.
 * Belongs to a city and has its own BookingService.
 * Uses ReentrantLock to handle concurrent show addition by admins.
 */
public class Theatre {
    private final String theatreId;
    private final String name;
    private final String city;
    private final List<Screen> screens;
    private final List<Show> shows = new ArrayList<>();
    private final BookingService bookingService;
    private final ReentrantLock showLock = new ReentrantLock();
    private int showCounter = 0;

    public Theatre(String theatreId, String name, String city,
                   List<Screen> screens, BookingService bookingService) {
        this.theatreId = theatreId;
        this.name = name;
        this.city = city;
        this.screens = Collections.unmodifiableList(new ArrayList<>(screens));
        this.bookingService = bookingService;
    }

    /**
     * Add a show to this theatre. Thread-safe for concurrent admin additions.
     * Validates no overlapping show on the same screen.
     */
    public Show addShow(Movie movie, Screen screen, long startTime) {
        showLock.lock();
        try {
            // Validate: no overlapping show on same screen
            for (Show existing : shows) {
                if (existing.screen().screenId().equals(screen.screenId())) {
                    long existingEnd = existing.startTime()
                            + existing.movie().durationMinutes() * 60_000L;
                    long newEnd = startTime + movie.durationMinutes() * 60_000L;
                    // Overlap if ranges intersect
                    if (startTime < existingEnd && newEnd > existing.startTime()) {
                        throw new IllegalStateException(
                                "Screen " + screen.name() + " is occupied at that time by: "
                                        + existing.movie().title());
                    }
                }
            }
            String id = "SH-" + (++showCounter);
            Show show = new Show(id, movie, screen, startTime);
            shows.add(show);
            bookingService.registerShow(show);
            return show;
        } finally {
            showLock.unlock();
        }
    }

    /** Get all movies currently showing at this theatre. */
    public List<Movie> getMovies() {
        return shows.stream()
                .map(Show::movie)
                .distinct()
                .collect(Collectors.toList());
    }

    /** Get all shows for a particular movie. */
    public List<Show> getShowsForMovie(Movie movie) {
        return shows.stream()
                .filter(s -> s.movie().movieId().equals(movie.movieId()))
                .collect(Collectors.toList());
    }

    public String theatreId() { return theatreId; }
    public String name() { return name; }
    public String city() { return city; }
    public List<Screen> screens() { return screens; }
    public List<Show> shows() { return Collections.unmodifiableList(shows); }
    public BookingService bookingService() { return bookingService; }

    @Override
    public String toString() { return name + " (" + city + ")"; }
}
