package com.portfolio.auctionmarket.domain.products.dto;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionResponse;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailAndAuctionResponse {
    private ProductDetailResponse productDetailResponse;

    private AuctionResponse auctionResponse;

    public static ProductDetailAndAuctionResponse from(Product entity) {
        return ProductDetailAndAuctionResponse.builder()
                .productDetailResponse(ProductDetailResponse.from(entity))
                .auctionResponse(AuctionResponse.from(entity.getAuction()))
                .build();
    }
}
