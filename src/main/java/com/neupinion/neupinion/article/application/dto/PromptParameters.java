package com.neupinion.neupinion.article.application.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromptParameters {

    private final String type = "object";
    private final Map<String, Object> properties = Map.of(
        "category", Map.of("type", "string", "enum", new String[]{"유리", "불리", "중립", "무관"}),
        "reason", Map.of("type", "string", "enum", new String[]{"기사 내용의 관점을 그렇게 판단한 이유를 존댓말로 반환한다."})
    );
    private final List<String> required = List.of("category", "reason");
}
