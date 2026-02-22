package com.portfolio.auctionmarket.domain.ai.service;

import com.portfolio.auctionmarket.domain.ai.client.GroqApiClient;
import com.portfolio.auctionmarket.domain.ai.client.LocalEmbeddingClient;
import com.portfolio.auctionmarket.domain.ai.client.PineconeApiClient;
import com.portfolio.auctionmarket.domain.ai.dto.AiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final LocalEmbeddingClient embeddingClient;
    private final PineconeApiClient pineconeApiClient;
    private final GroqApiClient groqApiClient;
    private final ChatHistoryService chatHistoryService;

    public Flux<AiResponse> askAuctionBot(String sessionId, String userQuery) {
        log.info("AI 상담 시작 - 세션: {}, 질문: {}", sessionId, userQuery);

        chatHistoryService.saveMessage(sessionId, "user", userQuery);

        List<Map<String, Object>> history = chatHistoryService.getHistory(sessionId);

        StringBuilder fullAiResponse = new StringBuilder();

        return embeddingClient.getEmbedding(userQuery)
                .flatMap(vector -> pineconeApiClient.retrieveContext(vector))
                .flatMapMany(context -> groqApiClient.askGroqWithContext(context, userQuery, history))
                .doOnNext(response -> {
                    if (response.getAnswer() != null) {
                        fullAiResponse.append(response.getAnswer());
                    }
                })
                .doOnComplete(() -> {
                    String finalAnswer = fullAiResponse.toString();
                    chatHistoryService.saveMessage(sessionId, "assistant", finalAnswer);
                    log.info("AI 최종 응답 Redis 저장 완료: {}", finalAnswer);
                });
    }

}
