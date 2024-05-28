package com.neupinion.neupinion.article.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neupinion.neupinion.article.application.dto.ArticleSearchRequest;
import com.neupinion.neupinion.article.application.dto.OpenAiPromptRequest;
import com.neupinion.neupinion.article.application.dto.OpenAiResponse;
import com.neupinion.neupinion.article.exception.ArticleException.UnorganizedResultException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatGptService {

    private static final String FORMATTED_STRING = "이 이슈는 %s 에 대한 내용이야. 이 이슈는 각각 %s 입장으로 나뉘어 있어. 이 이슈의 자세한 설명은 다음과 같아. %s \n 아래에 있는 기사의 내용이 %s 입장에 대해서 유리하게 쓰여져 있는지/중립적으로 쓰여져 있는지/불리하게 쓰여져 있는지/무관하게 쓰여졌는지 분류해줘. \n\n %s \n '분류: '유리/불리/중립/무관' 중 하나의 값, 이유: 네가 그렇게 판단한 이유' 형태로 대답해 줘";

    @Value("${openai.secret-key}")
    private String secretKey;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public Mono<OpenAiResponse> getAnalyzedResult(final ArticleSearchRequest request, final String articleBody) {
        final String joinedStands = String.join(",", request.getStands());
        final String prompt = String.format(FORMATTED_STRING, request.getSearchKeyword(), joinedStands,
                                            request.getIssueDescription(), request.getSelectedStand(),
                                            articleBody);
        final OpenAiPromptRequest promptRequest = new OpenAiPromptRequest(prompt);

        return webClient.post()
            .uri("/chat/completions")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(promptRequest), OpenAiPromptRequest.class)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .handle((jsonNode, sink) -> {
                final String result = jsonNode.get("choices").get(0).get("message").get("function_call")
                    .get("arguments")
                    .asText();
                System.out.println(result);
                try {
                    final JsonNode argumentsNode = objectMapper.readTree(result);
                    final OpenAiResponse response = new OpenAiResponse(argumentsNode.get("category").asText(),
                                                                       argumentsNode.get("reason").asText());
                    sink.next(response);
                } catch (Exception e) {
                    sink.error(new UnorganizedResultException(Map.of("result", result)));
                }
            });
    }
}
