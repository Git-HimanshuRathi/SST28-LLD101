package com.example.booking;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Platform-level service providing the required APIs:
 *
 *   showTheatres(userCity) : List<Theatre>
 *   showMovies(city)       : List<Movie> in theatres in the city
 *   bookTickets(show_id, seats) : MovieTicket (delegated to BookingService)
 *
 * User flow:
 *   1. User searches by city → showTheatres(city) or showMovies(city)
 *   2. If movies: user picks a movie → getTheatresForMovie(city, movie)
 *        → picks a theatre → picks a show → books seats
 *   3. If theatres: user picks a theatre → getMoviesInTheatre(theatre)
 *        → picks a movie → picks a show → books seats
 */
public class MovieTicketBookingService {
    private final Map<String, City> cities = new HashMap<>();  // cityId -> City

    public void addCity(City city) {
        cities.put(city.cityId(), city);
    }

    // ─── API: showTheatres(userCity) ─────────────────────────────
    /** Returns all theatres in a given city. */
    public List<Theatre> showTheatres(String cityName) {
        return cities.values().stream()
                .filter(c -> c.name().equalsIgnoreCase(cityName))
                .flatMap(c -> c.theatres().stream())
                .collect(Collectors.toList());
    }

    // ─── API: showMovies(city) ───────────────────────────────────
    /** Returns all unique movies currently showing in any theatre in the city. */
    public List<Movie> showMovies(String cityName) {
        return showTheatres(cityName).stream()
                .flatMap(t -> t.getMovies().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // ─── User flow: movie-first path ────────────────────────────
    /** Given a city and a movie, return theatres showing that movie. */
    public List<Theatre> getTheatresForMovie(String cityName, Movie movie) {
        return showTheatres(cityName).stream()
                .filter(t -> t.getMovies().stream()
                        .anyMatch(m -> m.movieId().equals(movie.movieId())))
                .collect(Collectors.toList());
    }

    /** Get all shows for a movie at a specific theatre. */
    public List<Show> getShowsForMovieAtTheatre(Theatre theatre, Movie movie) {
        return theatre.getShowsForMovie(movie);
    }

    // ─── User flow: theatre-first path ──────────────────────────
    /** Given a theatre, return all movies playing there. */
    public List<Movie> getMoviesInTheatre(Theatre theatre) {
        return theatre.getMovies();
    }

    /** Get all shows in a theatre for a specific movie. */
    public List<Show> getShowsInTheatre(Theatre theatre, Movie movie) {
        return theatre.getShowsForMovie(movie);
    }
}
