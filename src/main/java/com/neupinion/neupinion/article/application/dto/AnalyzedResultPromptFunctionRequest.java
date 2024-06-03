package com.neupinion.neupinion.article.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyzedResultPromptFunctionRequest {

    private final String name;
    private final String description;
    private final AnalyzedResultPromptParameters parameters;
}
