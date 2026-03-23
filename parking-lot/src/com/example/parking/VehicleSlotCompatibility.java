package com.example.parking;

import java.util.*;

public class VehicleSlotCompatibility {
    private static final Map<VehicleType, List<SlotType>> map = new HashMap<>();
    static {
        map.put(VehicleType.TWO_WHEELER, Arrays.asList(SlotType.SMALL, SlotType.MEDIUM, SlotType.LARGE));
        map.put(VehicleType.CAR, Arrays.asList(SlotType.MEDIUM, SlotType.LARGE));
        map.put(VehicleType.BUS, Collections.singletonList(SlotType.LARGE));
    }

    public static List<SlotType> compatibleSlots(VehicleType vt) {
        return map.getOrDefault(vt, Collections.emptyList());
    }
}
