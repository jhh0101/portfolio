package com.portfolio.auctionmarket.domain.ai.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.auctionmarket.domain.ai.dto.AiResponse;
import com.portfolio.auctionmarket.domain.ai.dto.GroqResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GroqApiClient {
    private final WebClient groqWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public GroqApiClient(@Qualifier("groqWebClient") WebClient webClient) {
        this.groqWebClient = webClient;
    }

    public Flux<AiResponse> askGroqWithContext(String context, String userMessage, List<Map<String, Object>> history) {
        log.info("파인콘에서 찾은 지식: {}", context);
        log.info("사용자가 한 질문: {}", userMessage);

        String systemPrompt = "너는 경매장 홈페이지의 친절한 상담원이야. "
                + "반드시 아래 제공된 [경매장 규칙]을 바탕으로 대답해. "
                + "규칙에 없는 내용이라면 지어내지 말고 '해당 내용은 확인이 필요합니다'라고 정중히 대답해.\n\n"
                + "[경매장 규칙]:\n" + context;

        List<Map<String, Object>> messages = new ArrayList<>();

        messages.add(Map.of("role", "system", "content", systemPrompt));

        if (history != null && !history.isEmpty()) {
            messages.addAll(history);
        }

        messages.add(Map.of("role", "user", "content", userMessage));

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", messages,
                    "stream", true
            );

            return groqWebClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .filter(data -> !data.trim().equals("[DONE]"))
                    .map(data -> {
                        try {
                            GroqResponse response = objectMapper.readValue(data, GroqResponse.class);
                            return AiResponse.from(response.extractContent());
                        } catch (Exception e) {
                            return AiResponse.from("");
                        }
                    })
                    .onErrorResume(e -> {
                        log.error("스트리밍 중 에러 발생: {}", e.getMessage());
                        return Flux.just(AiResponse.from("서비스 연결이 원활하지 않습니다."));
                    });
        } catch (Exception e) {
            log.error("Groq API 에러 발생!", e);
            throw new RuntimeException(e);
        }
    }
}
