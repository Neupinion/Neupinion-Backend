package com.neupinion.neupinion.article.domain;

import com.neupinion.neupinion.article.exception.ArticleException.UnsupportedStandException;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stand {
    ADVANTAGEOUS("유리"), DISADVANTAGEOUS("불리"), NEUTRAL("중립"), IRRELEVANT("무관");

    private final String value;

    public static Stand fromByValue(final String value) {
        return Arrays.stream(Stand.values())
            .filter(stand -> stand.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new UnsupportedStandException(Map.of("value", value)));
    }

    public static Stand fromByName(final String name) {
        return Arrays.stream(Stand.values())
            .filter(stand -> stand.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new UnsupportedStandException(Map.of("name", name)));
    }
}
