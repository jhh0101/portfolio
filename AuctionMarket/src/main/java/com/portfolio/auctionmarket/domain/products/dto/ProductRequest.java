package com.portfolio.auctionmarket.domain.products.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private Long sellerId;


    private Long category;


    private String title;


    private String description;


    private String status;
}
