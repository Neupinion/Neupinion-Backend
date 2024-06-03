package com.neupinion.neupinion.article.application.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyzedResultPromptParameters {

    private final String type = "object";
    private final Map<String, Object> properties = Map.of(
        "category", Map.of("type", "string", "enum", new String[]{"유리", "불리", "중립", "무관"}),
        "reason", Map.of("type", "string", "description", "기사가 주어진 입장에 대해 '유리/불리/중립/무관한' 관점을 가지고 있다고 판단한 이유에 대해 경어체로 작성해줘.")
    );
    private final List<String> required = List.of("category", "reason");
}
