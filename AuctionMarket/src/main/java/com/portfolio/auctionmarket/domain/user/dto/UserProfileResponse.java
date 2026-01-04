package com.portfolio.auctionmarket.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.util.MaskingUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private String username;
    private String phone;
    private String nickname;
    private Role role;
    private SellerStatus sellerStatus;
    private Long point;
    private Double avgRating;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static UserProfileResponse from(User user){
        String formatPhone = MaskingUtil.formatPhone(user.getPhone());

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(formatPhone)
                .nickname(user.getNickname())
                .role(user.getRole())
                .sellerStatus(user.getSellerStatus())
                .point(user.getPoint())
                .avgRating(user.getAvgRating())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
