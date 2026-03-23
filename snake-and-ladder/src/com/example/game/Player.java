package com.example.game;

public class Player {
    private final String name;
    private int position;
    private boolean hasWon;

    public Player(String name) { this.name = name; this.position = 0; }

    public String name() { return name; }
    public int position() { return position; }
    public boolean hasWon() { return hasWon; }
    public void setPosition(int pos) { this.position = pos; }
    public void markWon() { this.hasWon = true; }

    @Override public String toString() { return name + "@" + position; }
}
