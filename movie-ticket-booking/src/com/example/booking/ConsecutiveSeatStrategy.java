package com.example.booking;

import java.util.*;
import java.util.stream.Collectors;

public class ConsecutiveSeatStrategy implements SeatSelectionStrategy {
    @Override
    public List<ShowSeat> selectSeats(List<ShowSeat> available, int count, SeatType type) {
        // Filter by seat type and sort by row + seat number for consecutive picking
        List<ShowSeat> filtered = available.stream()
                .filter(ss -> ss.seat().type() == type)
                .sorted((a, b) -> {
                    int rowCmp = a.seat().row().compareTo(b.seat().row());
                    return rowCmp != 0 ? rowCmp : Integer.compare(a.seat().seatNumber(), b.seat().seatNumber());
                })
                .collect(Collectors.toList());

        if (filtered.size() < count) return Collections.emptyList();

        // Try to find consecutive seats in the same row
        for (int i = 0; i <= filtered.size() - count; i++) {
            List<ShowSeat> candidate = new ArrayList<>();
            candidate.add(filtered.get(i));
            for (int j = 1; j < count; j++) {
                ShowSeat prev = filtered.get(i + j - 1);
                ShowSeat curr = filtered.get(i + j);
                if (prev.seat().row().equals(curr.seat().row())
                        && curr.seat().seatNumber() == prev.seat().seatNumber() + 1) {
                    candidate.add(curr);
                } else {
                    break;
                }
            }
            if (candidate.size() == count) return candidate;
        }

        // Fallback: return first N available seats of the type
        return filtered.subList(0, count);
    }
}
