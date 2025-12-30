package com.portfolio.auctionmarket.domain.products.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {

    private Long productId;
    private String seller;
    private String category;
    private String title;
    private String description;
    private ProductStatus productStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static ProductDetailResponse from(Product entity) {
        return ProductDetailResponse.builder()
                .productId(entity.getProductId())
                .seller(entity.getSeller().getNickname())
                .category(entity.getCategory().getCategory())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .productStatus(entity.getProductStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
