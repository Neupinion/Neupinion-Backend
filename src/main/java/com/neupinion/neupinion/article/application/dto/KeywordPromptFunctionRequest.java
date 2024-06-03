package com.neupinion.neupinion.article.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KeywordPromptFunctionRequest {

    private final String name = "get_keywords";
    private final String description = "기사의 내용에서 특정 입장에 유리한 키워드 5가지, 불리한 키워드 5가지를 추출한다.";
    private final KeywordPromptParameters parameters = new KeywordPromptParameters();
}
