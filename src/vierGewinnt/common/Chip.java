package vierGewinnt.common;

import java.util.HashMap;
import java.util.Map;

public enum Chip {

	NORMAL(1), EXPLOSIVE(2), EMPTY(3);

    private int value;
    private static Map<Integer, Chip> map = new HashMap<>();

    private Chip(int value) {
        this.value = value;
    }

    static {
        for (Chip chipType : Chip.values()) {
            map.put(chipType.value, chipType);
        }
    }

    public static Chip valueOf(int chipType) {
        return (Chip) map.get(chipType);
    }

    public int getValue() {
        return value;
    }
	
}
