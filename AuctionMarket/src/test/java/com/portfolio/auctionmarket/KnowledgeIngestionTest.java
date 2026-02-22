package com.portfolio.auctionmarket;

import com.portfolio.auctionmarket.domain.ai.client.LocalEmbeddingClient;
import com.portfolio.auctionmarket.domain.ai.client.PineconeApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class KnowledgeIngestionTest {

    @Autowired
    LocalEmbeddingClient localEmbeddingClient;
    @Autowired
    PineconeApiClient pineconeClient;

    @Test
    void 주입_시작() {
        List<String> rules = List.of(
                "우리 경매장의 낙찰 수수료는 최종 낙찰가의 5%입니다.",
                "경매 낙찰 후 취소 시 입찰 보증금은 반환되지 않습니다.",
                "낙찰 물품은 결제 후 3일 이내에 발송되어야 합니다."
        );

        for (int i = 0; i < rules.size(); i++) {
            String text = rules.get(i);
            String id = "rule-" + (i + 1);

            // 1. 텍스트를 숫자로 변환 (HuggingFace)
            List<Double> vector = localEmbeddingClient.getEmbedding(text).block();

            // 2. 숫자를 파인콘에 저장 (Pinecone)
            if (vector != null && !vector.isEmpty()) {
                pineconeClient.upsert(id, vector, text).block();
            }
        }
        System.out.println("✅ 지식 주입 완료!");
    }
}