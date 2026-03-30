package com.example.booking;

import java.util.List;

public interface SeatSelectionStrategy {
    List<ShowSeat> selectSeats(List<ShowSeat> available, int count, SeatType type);
}
