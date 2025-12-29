package com.portfolio.auctionmarket.domain.bids.dto;

import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResponse {

    private Long bidId;
    private String auction;
    private String nickname;
    private Integer bidPrice;

    public static BidResponse from(Bid entity) {
        return BidResponse.builder()
                .bidId(entity.getBidId())
                .auction(entity.getAuction().getProduct().getTitle())
                .nickname(entity.getBidder().getNickname())
                .bidPrice(entity.getBidPrice())
                .build();
    }
}
