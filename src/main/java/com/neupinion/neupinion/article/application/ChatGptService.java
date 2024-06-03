package com.neupinion.neupinion.article.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neupinion.neupinion.article.application.dto.AnalyzedResultPromptRequest;
import com.neupinion.neupinion.article.application.dto.AnalyzedResultResponse;
import com.neupinion.neupinion.article.application.dto.ArticleSearchRequest;
import com.neupinion.neupinion.article.application.dto.KeywordPromptRequest;
import com.neupinion.neupinion.article.application.dto.KeywordResponse;
import com.neupinion.neupinion.article.exception.ArticleException.UnorganizedResultException;
import com.neupinion.neupinion.issue.domain.IssueStand;
import java.util.ArrayList;
import java.util.List;
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

    private static final String ANALYZED_RESULT_FORMATTED_STRING = "이 이슈는 %s 에 대한 내용이야. 이 이슈는 각각 %s 입장으로 나뉘어 있어. 이 이슈의 자세한 설명은 다음과 같아. %s \n 아래에 있는 기사의 내용이 %s 입장에 대해서 유리하게 쓰여져 있는지/중립적으로 쓰여져 있는지/불리하게 쓰여져 있는지/무관하게 쓰여졌는지 분류해줘. \n\n %s";
    private static final String KEYWORD_FORMATTED_STRING = "%s 두 입장을 모두 다루는 아래의 기사에서 독자 입장에서 각 입장에 대해 유의해야 할 단어들을 골라줘. %s 의 입장에서 유의해야 하는 내용 5가지, %s 의 입장에서 유의해야 하는 내용 5가지를 뽑되, 하나 당 두 세 단어 정도로 짧게 만들어줘. 키워드에 최대한 중복된 단어가 들어가지 않도록 해줘. \n %s";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    @Value("${openai.secret-key}")
    private String secretKey;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public Mono<AnalyzedResultResponse> getAnalyzedResult(final ArticleSearchRequest request,
                                                          final String articleBody) {
        final String joinedStands = String.join(",", request.getStands());
        final String prompt = String.format(ANALYZED_RESULT_FORMATTED_STRING, request.getSearchKeyword(), joinedStands,
                                            request.getIssueDescription(), request.getSelectedStand(),
                                            articleBody);
        final AnalyzedResultPromptRequest promptRequest = new AnalyzedResultPromptRequest(prompt);

        return webClient.post()
                        .uri("/chat/completions")
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_PREFIX + secretKey)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(promptRequest), AnalyzedResultPromptRequest.class)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .handle((jsonNode, sink) -> {
                            final String result = jsonNode.get("choices").get(0).get("message").get("function_call")
                                                          .get("arguments")
                                                          .asText();
                            System.out.println(result);
                            try {
                                final JsonNode argumentsNode = objectMapper.readTree(result);
                                final AnalyzedResultResponse response = new AnalyzedResultResponse(
                                    argumentsNode.get("category").asText(),
                                    argumentsNode.get("reason").asText());
                                sink.next(response);
                            } catch (Exception e) {
                                sink.error(new UnorganizedResultException(Map.of("result", result)));
                            }
                        });
    }

    public Mono<KeywordResponse> getKeywords(final String articleBody, final List<IssueStand> stands) {
        final String joinedStands = stands.stream()
                                          .map(IssueStand::getStand)
                                          .reduce((stand1, stand2) -> stand1 + ", " + stand2)
                                          .orElse("");
        final String prompt = String.format(KEYWORD_FORMATTED_STRING, joinedStands, stands.get(0).getStand(),
                                            stands.get(1).getStand(), articleBody);

        return webClient.post()
                        .uri("/chat/completions")
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_PREFIX + secretKey)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(new KeywordPromptRequest(prompt)), KeywordPromptRequest.class)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .handle((jsonNode, sink) -> {
                            final String result = jsonNode.get("choices").get(0).get("message").get("function_call")
                                                          .get("arguments")
                                                          .asText();
                            System.out.println(result);
                            try {
                                final JsonNode argumentsNode = objectMapper.readTree(result);
                                final List<String> firstKeywords = new ArrayList<>();
                                final List<String> secondKeywords = new ArrayList<>();

                                argumentsNode.get("firstKeywords").forEach(keywordNode ->
                                                                               firstKeywords.add(
                                                                                   keywordNode.asText()));
                                argumentsNode.get("secondKeywords").forEach(keywordNode ->
                                                                                secondKeywords.add(
                                                                                    keywordNode.asText()));
                                final KeywordResponse response = new KeywordResponse(stands.get(0).getStand(),
                                                                                     firstKeywords,
                                                                                     stands.get(1).getStand(),
                                                                                     secondKeywords);
                                sink.next(response);
                            } catch (Exception e) {
                                sink.error(new UnorganizedResultException(Map.of("result", result)));
                            }
                        });
    }
}
