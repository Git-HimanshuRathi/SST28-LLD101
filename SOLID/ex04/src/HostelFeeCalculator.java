import java.util.*;

/**
 * OCP-compliant: uses RoomPricingRegistry and AddOn enum prices
 * instead of switch-case and if/else branching.
 */
public class HostelFeeCalculator {
    private final BookingRepository repo;
    private final RoomPricingRegistry roomPricingRegistry;

    public HostelFeeCalculator(BookingRepository repo, RoomPricingRegistry roomPricingRegistry) {
        this.repo = repo;
        this.roomPricingRegistry = roomPricingRegistry;
    }

    public void process(BookingRequest req) {
        Money monthly = calculateMonthly(req);
        Money deposit = new Money(5000.00);

        ReceiptPrinter.print(req, monthly, deposit);

        String bookingId = "H-" + (7000 + new Random(1).nextInt(1000));
        repo.save(bookingId, req, monthly, deposit);
    }

    private Money calculateMonthly(BookingRequest req) {
        RoomPricing roomPricing = roomPricingRegistry.getFor(req.roomType);
        double base = roomPricing.basePrice();

        double addOnTotal = 0.0;
        for (AddOn a : req.addOns) {
            addOnTotal += a.getPrice();
        }

        return new Money(base + addOnTotal);
    }
}
