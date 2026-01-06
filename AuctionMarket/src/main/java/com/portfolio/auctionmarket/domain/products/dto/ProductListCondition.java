package com.portfolio.auctionmarket.domain.products.dto;

import com.portfolio.auctionmarket.domain.user.entity.UserStatus;

public record ProductListCondition(Long userId, String title, String path, String sort) {}
