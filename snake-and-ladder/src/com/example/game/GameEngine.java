package com.example.game;

import java.util.*;

public class GameEngine {
    private final Board board;
    private final Dice dice;
    private final Queue<Player> players;
    private final List<Player> winners = new ArrayList<>();

    public GameEngine(Board board, Dice dice, List<Player> playerList) {
        this.board = board; this.dice = dice;
        this.players = new LinkedList<>(playerList);
    }

    public void play() {
        System.out.println("\n=== Game Start ===");
        int maxTurns = 200;
        int turn = 0;
        while (winners.size() < players.size() - 1 && turn < maxTurns) {
            Player p = players.poll();
            if (p.hasWon()) continue;

            int rolled = dice.roll();
            int oldPos = p.position();
            int newPos = board.movePlayer(oldPos, rolled);
            p.setPosition(newPos);
            System.out.println(p.name() + " rolled " + rolled + ": " + oldPos + " -> " + newPos);

            if (newPos == board.maxCell()) {
                p.markWon();
                winners.add(p);
                System.out.println(p.name() + " WINS! (rank #" + winners.size() + ")");
            }
            players.add(p);
            turn++;
        }

        System.out.println("\n=== Results ===");
        for (int i = 0; i < winners.size(); i++) {
            System.out.println("#" + (i + 1) + " " + winners.get(i).name());
        }
    }
}
