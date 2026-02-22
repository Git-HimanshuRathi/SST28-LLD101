/**
 * OCP: Each AddOn enum constant knows its own price.
 * New add-ons can be added by extending the enum without editing calculator
 * logic.
 */
public enum AddOn {
    MESS(1000.0),
    LAUNDRY(500.0),
    GYM(300.0);

    private final double price;

    AddOn(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
