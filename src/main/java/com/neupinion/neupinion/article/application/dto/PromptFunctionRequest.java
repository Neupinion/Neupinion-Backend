package com.neupinion.neupinion.article.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromptFunctionRequest {

    private final String name = "get_categorized_response";
    private final String description = "기사의 내용이 선택된 입장에서 유리하게/불리하게/중립적으로/관련없이 쓰였는지 분류하고, 그 이유를 존댓말로 반환한다";
    private final PromptParameters parameters = new PromptParameters();
}
