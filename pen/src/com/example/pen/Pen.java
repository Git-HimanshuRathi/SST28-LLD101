package com.example.pen;

public class Pen {
    private final OpenCloseStrategy openClose;
    private Refill refill;

    public Pen(OpenCloseStrategy openClose, Refill refill) {
        this.openClose = openClose; this.refill = refill;
    }

    public void write(String text) {
        openClose.start();
        System.out.println("Writing '" + text + "' in " + refill.getColor());
        openClose.close();
    }

    public void changeRefill(Refill newRefill) {
        this.refill = newRefill;
        System.out.println("Refill changed to " + newRefill.getColor());
    }

    public Color getColor() { return refill.getColor(); }
}
