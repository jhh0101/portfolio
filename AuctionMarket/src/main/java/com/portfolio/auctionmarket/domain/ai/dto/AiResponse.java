package com.portfolio.auctionmarket.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AiResponse {
    private String answer;

    public static AiResponse from(String answer) {
        return AiResponse.builder()
                .answer(answer)
                .build();
    }
}