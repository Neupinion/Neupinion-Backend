package com.neupinion.neupinion.issue.domain;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Category {

    ENTERTAINMENTS("연예"),
    POLITICS("정치"),
    ECONOMY("경제"),
    SOCIETY("사회"),
    WORLD("세계"),
    SPORTS("스포츠");

    private final String value;

    Category(final String value) {
        this.value = value;
    }

    public static Category from(final String value) {
        return Arrays.stream(Category.values())
            .filter(category -> category.name().equalsIgnoreCase(value))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
    }
}
