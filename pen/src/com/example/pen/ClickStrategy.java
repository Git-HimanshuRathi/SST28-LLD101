package com.example.pen;

public class ClickStrategy implements OpenCloseStrategy {
    @Override public void start() { System.out.println("Click! Pen extended"); }
    @Override public void close() { System.out.println("Click! Pen retracted"); }
}
