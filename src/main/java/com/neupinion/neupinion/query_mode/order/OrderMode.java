package com.neupinion.neupinion.query_mode.order;

import java.util.Arrays;

public enum OrderMode {
    RECENT,
    POPULAR;

    public static OrderMode from(String orderMode) {
        return Arrays.stream(OrderMode.values())
            .filter(mode -> mode.name().equalsIgnoreCase(orderMode))
            .findFirst()
            .orElse(RECENT);
    }
}
