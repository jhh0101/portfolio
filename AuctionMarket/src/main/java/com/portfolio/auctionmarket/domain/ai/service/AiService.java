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

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final LocalEmbeddingClient embeddingClient;
    private final PineconeApiClient pineconeApiClient;
    private final GroqApiClient groqApiClient;

    public Flux<AiResponse> askAuctionBot(String userQuery) {
        log.info("🤖 AI 상담 시작 - 사용자 질문: {}", userQuery);

        return embeddingClient.getEmbedding(userQuery)
                .flatMap(vector -> pineconeApiClient.retrieveContext(vector))
                .flatMapMany(context -> groqApiClient.askGroqWithContext(context, userQuery));
    }

}
