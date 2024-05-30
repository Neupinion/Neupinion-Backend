package com.neupinion.neupinion.article.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAiResponse {

    private final String category;
    private final String reason;
}
