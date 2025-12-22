package com.portfolio.auctionmarket.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String username;
    private String nickname;
    private Role role;
    private String sellerStatus;
    private Long point;
    private Double avgRating;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static UserResponse from(User user){
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .sellerStatus(user.getSellerStatus())
                .point(user.getPoint())
                .avgRating(user.getAvgRating())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
