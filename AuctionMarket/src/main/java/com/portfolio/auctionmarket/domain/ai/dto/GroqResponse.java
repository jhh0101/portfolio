package com.portfolio.auctionmarket.domain.ai.dto;

import java.util.List;

public record GroqResponse(List<Choice> choices) {
    public record Choice(Delta delta, Message message) {}
    public record Delta(String content) {}
    public record Message(String content) {}

    public String extractContent() {
        if (choices != null && !choices.isEmpty()) {
            var delta = choices.getFirst().delta();
            // 마지막 null 조각(대답 끝)을 방어하는 핵심 로직
            if (delta != null && delta.content() != null) {
                return delta.content();
            }
        }
        return "";
    }
}