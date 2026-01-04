package com.portfolio.auctionmarket.domain.user.dto;

import com.portfolio.auctionmarket.domain.user.entity.UserStatus;

public record UserListCondition(String email, String nickname, UserStatus status) {}
