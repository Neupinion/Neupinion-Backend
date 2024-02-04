package com.neupinion.neupinion.issue.domain;

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
}
