package com.neupinion.neupinion.article.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordResponse {

    private final String selectedStand;
    private final List<String> positiveKeywords;
    private final List<String> negativeKeywords;
}
