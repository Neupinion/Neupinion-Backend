package com.neupinion.neupinion.article.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyzedResultPromptRequest {

    private final String model = "gpt-4-turbo";

    @JsonProperty("max_tokens")
    private final int maxTokens = 500;
    private final List<AnalyzedResultPromptFunctionRequest> functions = List.of(new AnalyzedResultPromptFunctionRequest(
        "get_categorized_response",
        "기사의 내용이 선택된 입장에서 유리하게/불리하게/중립적으로/관련없이 쓰였는지 분류하고, 그 이유를 존댓말로 반환한다",
        new AnalyzedResultPromptParameters()
    ));
    private final List<Map<String, Object>> messages = new ArrayList<>() {{
        add(Map.of("role", "system", "content", "너는 훌륭한 뉴스 분석가야."));
    }};

    @JsonProperty("function_call")
    private final Map<String, String> functionCall = Map.of("name", "get_categorized_response");

    public AnalyzedResultPromptRequest(final String formattedMessage) {
        this.messages.add(Map.of("role", "user", "content", formattedMessage));
    }
}
