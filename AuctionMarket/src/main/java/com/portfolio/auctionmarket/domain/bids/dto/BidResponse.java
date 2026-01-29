package com.portfolio.auctionmarket.domain.bids.dto;

public interface BidResponse {

    Long getBidId();
    Long getAuctionId();
    String getNickname();
    Long getBidPrice();

}