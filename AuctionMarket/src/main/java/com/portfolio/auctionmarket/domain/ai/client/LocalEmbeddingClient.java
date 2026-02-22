package com.portfolio.auctionmarket.domain.ai.client;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class LocalEmbeddingClient {
    // API 호출 없이 로컬 CPU에서 숫자로 변환합니다.
    private final EmbeddingModel model = new AllMiniLmL6V2QuantizedEmbeddingModel();

    public Mono<List<Double>> getEmbedding(String text) {
        return Mono.fromCallable(() -> {
            Embedding embedding = model.embed(text).content();
            return embedding.vectorAsList().stream()
                    .map(Float::doubleValue)
                    .toList();
        }).doOnNext(res -> log.info("✅ 로컬 엔진 임베딩 성공! 차원: {}", res.size()));
    }
}