package com.neupinion.neupinion.article.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    private static final String OPEN_AI_URL = "https://api.openai.com/v1";
    private final ObjectMapper objectMapper;

    public WebClientConfig() {
        this.objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());
    }

    @Bean
    public WebClient webClient() {
        final int bufferSize = 16 * 1024 * 1024;  // 16MB
        final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
            .codecs(configurer -> {
                configurer.defaultCodecs()
                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                configurer.defaultCodecs()
                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                configurer.defaultCodecs().maxInMemorySize(bufferSize);
            })
            .build();

        return WebClient.builder()
            .baseUrl(OPEN_AI_URL)
            .filter(errorHandlingFilter())
            .build();
    }

    public static ExchangeFilterFunction errorHandlingFilter() {
        return (clientRequest, next) -> next.exchange(clientRequest)
            .flatMap(clientResponse -> {
                if (clientResponse.statusCode().isError()) {
                    return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            String errorMessage = String.format(
                                "Error response from server. Status code: %s, body: %s, headers: %s",
                                clientResponse.statusCode(), errorBody, clientResponse.headers().asHttpHeaders());
                            return Mono.error(new RuntimeException(errorMessage));
                        });
                }
                return Mono.just(clientResponse);
            })
            .onErrorResume(e -> {
                String errorMessage = String.format("Error sending request to server. Error: %s", e.getMessage());
                return Mono.error(new RuntimeException(errorMessage, e));
            });
    }
}
