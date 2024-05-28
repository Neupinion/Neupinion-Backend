package com.neupinion.neupinion.article.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAiPromptRequest {

    private final String model = "gpt-3.5-turbo";

    @JsonProperty("max_tokens")
    private final int maxTokens = 500;
    private final List<PromptFunctionRequest> functions = List.of(new PromptFunctionRequest());
    private final List<Map<String, Object>> messages = new ArrayList<>() {{
        add(Map.of("role", "system", "content", "너는 훌륭한 뉴스 분석가야."));
    }};

    @JsonProperty("function_call")
    private final Map<String, String> functionCall = Map.of("name", "get_categorized_response");

    public OpenAiPromptRequest(final String formattedMessage) {
        this.messages.add(Map.of("role", "user", "content", formattedMessage));
    }
}
