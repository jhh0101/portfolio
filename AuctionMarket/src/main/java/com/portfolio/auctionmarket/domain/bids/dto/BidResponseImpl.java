package com.portfolio.auctionmarket.domain.bids.dto;

import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResponseImpl {

    private Long bidId;
    private String nickname;
    private Long bidPrice;

    public static BidResponseImpl from(BidResponse response) {
        return BidResponseImpl.builder()
                .bidId(response.getBidId())
                .nickname(response.getNickname())
                .bidPrice(response.getBidPrice())
                .build();
    }
}
