import java.util.*;

/**
 * OCP: Maps room type constants to RoomPricing implementations.
 * New room types can be added by registering a new RoomPricing.
 */
public class RoomPricingRegistry {
    private final Map<Integer, RoomPricing> pricings = new HashMap<>();

    public RoomPricingRegistry() {
        pricings.put(LegacyRoomTypes.SINGLE, new SingleRoom());
        pricings.put(LegacyRoomTypes.DOUBLE, new DoubleRoom());
        pricings.put(LegacyRoomTypes.TRIPLE, new TripleRoom());
        pricings.put(LegacyRoomTypes.DELUXE, new DeluxeRoom());
    }

    public void register(int roomType, RoomPricing pricing) {
        pricings.put(roomType, pricing);
    }

    public RoomPricing getFor(int roomType) {
        return pricings.getOrDefault(roomType, new DeluxeRoom());
    }
}
