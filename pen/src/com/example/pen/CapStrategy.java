package com.example.pen;

public class CapStrategy implements OpenCloseStrategy {
    @Override public void start() { System.out.println("Cap removed"); }
    @Override public void close() { System.out.println("Cap replaced"); }
}
