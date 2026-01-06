package com.portfolio.auctionmarket.domain.orders.dto;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String title;
    private String nickname;
    private Long finalPrice;

    public static OrderResponse from(Order entity) {
        if (entity == null) {
            return null;
        }
        return OrderResponse.builder()
                .orderId(entity.getOrderId())
                .title(entity.getAuction().getProduct().getTitle())
                .nickname(entity.getBuyer().getNickname())
                .finalPrice(entity.getFinalPrice())
                .build();
    }
}
