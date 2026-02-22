/**
 * Default discount rules: student >=180 gets 10; staff gets 5 or 15 based on
 * lines.
 */
public class DefaultDiscountCalculator implements DiscountCalculator {
    @Override
    public double discountAmount(String customerType, double subtotal, int distinctLines) {
        if ("student".equalsIgnoreCase(customerType)) {
            if (subtotal >= 180.0)
                return 10.0;
            return 0.0;
        }
        if ("staff".equalsIgnoreCase(customerType)) {
            if (distinctLines >= 3)
                return 15.0;
            return 5.0;
        }
        return 0.0;
    }
}
