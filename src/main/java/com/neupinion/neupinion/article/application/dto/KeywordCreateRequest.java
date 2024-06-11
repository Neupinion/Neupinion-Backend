package com.neupinion.neupinion.article.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordCreateRequest {

    private final String firstStand;
    private final List<String> firstKeywords;
    private final String secondStand;
    private final List<String> secondKeywords;
}
