package com.neupinion.neupinion.article.application.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KeywordPromptParameters {

    private final String type = "object";
    private final Map<String, Object> properties = Map.of(
        "positiveKeywords", Map.of(
            "type", "array",
            "description", "기사의 내용이 선택된 입장에서 유리한 내용 5가지를 하나 당 두 세 단어로 짧게 만들어줘.",
            "items", Map.of("type", "string"),
            "minItems", 5,
            "maxItems", 5
        ),
        "negativeKeywords", Map.of(
            "type", "array",
            "description", "기사의 내용이 선택된 입장에서 불리한 내용 5가지를 하나 당 두 세 단어로 짧게 만들어줘.",
            "items", Map.of("type", "string"),
            "minItems", 5,
            "maxItems", 5
        )
    );
    private final List<String> required = List.of("positiveKeywords", "negativeKeywords");
}
