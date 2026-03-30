package com.example.booking;

import java.util.*;

/**
 * Represents a city containing multiple theatres.
 * Users search by city to find theatres and movies.
 */
public class City {
    private final String cityId;
    private final String name;
    private final List<Theatre> theatres = new ArrayList<>();

    public City(String cityId, String name) {
        this.cityId = cityId;
        this.name = name;
    }

    public void addTheatre(Theatre theatre) {
        theatres.add(theatre);
    }

    public String cityId() { return cityId; }
    public String name() { return name; }
    public List<Theatre> theatres() { return Collections.unmodifiableList(theatres); }

    @Override
    public String toString() { return name; }
}
