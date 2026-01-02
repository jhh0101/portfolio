package com.portfolio.auctionmarket.domain.bids.dto;

import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidCancelResponse {

    private Long auctionId;
    private String title;
    private Long bidPrice;
    private Long currentPoint;

    public static BidCancelResponse from(Bid entity) {
        return BidCancelResponse.builder()
                .auctionId(entity.getAuction().getAuctionId())
                .title(entity.getAuction().getProduct().getTitle())
                .bidPrice(entity.getBidPrice())
                .currentPoint(entity.getBidder().getPoint())
                .build();
    }
}
