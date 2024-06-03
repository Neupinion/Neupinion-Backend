package com.neupinion.neupinion.article.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordPromptRequest {

    private final String model = "gpt-4-turbo";

    @JsonProperty("max_tokens")
    private final int maxTokens = 1000;
    private final List<KeywordPromptFunctionRequest> functions = List.of(new KeywordPromptFunctionRequest());
    private final List<Map<String, Object>> messages = new ArrayList<>();

    @JsonProperty("function_call")
    private final Map<String, String> functionCall = Map.of("name", "get_keywords");

    public KeywordPromptRequest(final String formattedMessage) {
        this.messages.add(Map.of("role", "user", "content", formattedMessage));
    }
}
