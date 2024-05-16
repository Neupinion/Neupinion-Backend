package com.neupinion.neupinion.query_mode.view.opinion;

import java.util.Arrays;

public enum OpinionViewMode {
    ALL, DOUBT, TRUST;

    public static OpinionViewMode from(final String mode) {
        return Arrays.stream(values())
            .filter(viewMode -> viewMode.name().equalsIgnoreCase(mode))
            .findFirst()
            .orElse(ALL);
    }
}
