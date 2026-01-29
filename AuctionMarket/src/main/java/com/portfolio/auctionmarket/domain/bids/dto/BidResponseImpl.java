package com.portfolio.auctionmarket.domain.bids.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResponseImpl {

    private Long bidId;
    private Long auctionId;
    private String nickname;
    private Long bidPrice;

    public static BidResponseImpl from(BidResponse response) {
        return BidResponseImpl.builder()
                .bidId(response.getBidId())
                .auctionId(response.getAuctionId())
                .nickname(response.getNickname())
                .bidPrice(response.getBidPrice())
                .build();
    }
}
