package com.portfolio.auctionmarket.domain.ai.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class PineconeApiClient {
    private final WebClient webClient;

    public PineconeApiClient(@Qualifier("pineconeWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> retrieveContext(List<Double> vector) {
        var requestBody = Map.of(
                "vector", vector,
                "topK", 3,
                "includeMetadata", true
        );

        return webClient.post()
                .uri("/query")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(res -> log.info("파인콘 검색 결과 수신 완료"))
                .onErrorResume(e -> {
                    log.info("pinecone 검색 중 에러 {}", e);
                    return Mono.just("");
                });
    }

    public Mono<Void> upsert(String id, List<Double> values, String content) {
        Map<String, Object> vector = Map.of(
                "id", id,
                "values", values,
                "metadata", Map.of("text", content)
        );

        Map<String, Object> requestBody = Map.of(
                "vectors", List.of(vector)
        );

        return webClient.post()
                .uri("/vectors/upsert")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("🚀 파인콘 지식 저장 완료! ID: {}", id));
    }
}
