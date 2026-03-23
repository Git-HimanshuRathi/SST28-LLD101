package com.example.pen;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Pen Design ===");

        Pen capPen = new Pen(new CapStrategy(), new Refill(Color.BLUE));
        capPen.write("Hello");

        System.out.println();

        Pen clickPen = new Pen(new ClickStrategy(), new Refill(Color.BLACK));
        clickPen.write("World");

        System.out.println();

        clickPen.changeRefill(new Refill(Color.RED));
        clickPen.write("Important!");
    }
}
