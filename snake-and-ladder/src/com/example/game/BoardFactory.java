package com.example.game;

import java.util.*;

public class BoardFactory {
    public static Board createBoard(int size, DifficultyLevel diff) {
        Map<Integer, Integer> snakes = new LinkedHashMap<>();
        Map<Integer, Integer> ladders = new LinkedHashMap<>();
        int max = size * size;

        if (diff == DifficultyLevel.EASY) {
            snakes.put(17, 7); snakes.put(54, 34); snakes.put(62, 19);
            ladders.put(3, 22); ladders.put(5, 8); ladders.put(20, 29);
            ladders.put(27, 56); ladders.put(36, 55); ladders.put(51, 67);
        } else {
            snakes.put(17, 7); snakes.put(54, 34); snakes.put(62, 19);
            snakes.put(87, 24); snakes.put(95, 75); snakes.put(98, 79);
            ladders.put(3, 22); ladders.put(20, 29); ladders.put(27, 56);
        }

        System.out.println("Board created (" + size + "x" + size + ", " + diff + ")");
        System.out.println("Snakes: " + snakes);
        System.out.println("Ladders: " + ladders);
        return new Board(size, snakes, ladders);
    }
}
