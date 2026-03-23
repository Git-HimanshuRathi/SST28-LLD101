package com.example.game;

import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Snake and Ladder ===");
        Board board = BoardFactory.createBoard(10, DifficultyLevel.EASY);
        Dice dice = new Dice(6);
        List<Player> players = Arrays.asList(new Player("Alice"), new Player("Bob"));
        GameEngine engine = new GameEngine(board, dice, players);
        engine.play();
    }
}
