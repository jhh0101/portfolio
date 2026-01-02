package com.portfolio.auctionmarket.domain.bids.dto;

import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResponse {

    private Long bidId;
    private String nickname;
    private Long bidPrice;

    public static BidResponse from(Bid entity) {
        return BidResponse.builder()
                .bidId(entity.getBidId())
                .nickname(entity.getBidder().getNickname())
                .bidPrice(entity.getBidPrice())
                .build();
    }
}
