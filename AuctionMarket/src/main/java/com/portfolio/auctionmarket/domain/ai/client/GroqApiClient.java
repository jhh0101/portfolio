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

    public Flux<AiResponse> askGroq(String userMessage) {
        log.info("사용자가 질문 전송 : {}", userMessage);

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", List.of(
                            Map.of("role", "system", "content", "너는 경매장 홈페이지의 친절한 상담원이야. 짧고 정확, 정중하게 한국어 존댓말로 대답해야 해."),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "stream", true
            );

            return groqWebClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class) // ⭐️ 1. 일단 무조건 날것의 문자열(String)로 다 받습니다!
                    .filter(data -> !data.trim().equals("[DONE]")) // ⭐️ 2. 범인 검거! "[DONE]"이 오면 통과시키지 않고 버립니다.
                    .map(data -> {
                        try {
                            // 3. "[DONE]"이 아닌 정상적인 JSON 문자열만 GroqResponse 객체로 수동 변환합니다.
                            GroqResponse response = objectMapper.readValue(data, GroqResponse.class);
                            return AiResponse.from(response.extractContent());
                        } catch (Exception e) {
                            log.error("JSON 파싱 에러 발생 데이터: {}", data);
                            return AiResponse.from("");
                        }
                    })
                    .doOnNext(aiResponse -> log.info("전송 중인 조각 : {}", aiResponse.getAnswer()))
                    .onErrorResume(e -> {
                        log.error("스트리밍 중 에러 발생: {}", e.getMessage());
                        return Flux.just(AiResponse.from("서비스 연결이 원활하지 않습니다."));
                    });
        } catch (WebClientResponseException e) {
            log.error("Groq API 에러 발생! 상태코드: {}, 응답본문: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

}
