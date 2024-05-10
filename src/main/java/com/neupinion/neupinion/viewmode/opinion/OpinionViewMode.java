package com.neupinion.neupinion.viewmode.opinion;

import com.neupinion.neupinion.opinion.exception.OpinionException.InvalidViewModeException;
import java.util.Arrays;

public enum OpinionViewMode {
    ALL, DOUBT, TRUST;

    public static OpinionViewMode from(final String mode) {
        return Arrays.stream(values())
            .filter(viewMode -> viewMode.name().equalsIgnoreCase(mode))
            .findFirst()
            .orElseThrow(InvalidViewModeException::new);
    }
}
