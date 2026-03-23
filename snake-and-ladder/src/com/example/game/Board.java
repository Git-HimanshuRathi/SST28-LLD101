package com.example.game;

import java.util.Map;

public class Board {
    private final int size;
    private final int maxCell;
    private final Map<Integer, Integer> snakeMap;
    private final Map<Integer, Integer> ladderMap;

    public Board(int size, Map<Integer, Integer> snakeMap, Map<Integer, Integer> ladderMap) {
        this.size = size; this.maxCell = size * size;
        this.snakeMap = snakeMap; this.ladderMap = ladderMap;
    }

    public int maxCell() { return maxCell; }

    public int movePlayer(int currentPos, int diceValue) {
        int newPos = currentPos + diceValue;
        if (newPos > maxCell) return currentPos;
        if (snakeMap.containsKey(newPos)) {
            System.out.println("  Bitten by snake at " + newPos + " -> " + snakeMap.get(newPos));
            newPos = snakeMap.get(newPos);
        } else if (ladderMap.containsKey(newPos)) {
            System.out.println("  Climbed ladder at " + newPos + " -> " + ladderMap.get(newPos));
            newPos = ladderMap.get(newPos);
        }
        return newPos;
    }
}
