package com.neupinion.neupinion.utils;

import java.util.Objects;

public class StringChecker {

    public static boolean isNullOrBlank(final String value) {
        return Objects.isNull(value) || value.isBlank();
    }
}
