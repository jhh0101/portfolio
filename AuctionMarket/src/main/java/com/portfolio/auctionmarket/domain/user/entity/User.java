package com.portfolio.auctionmarket.domain.user.entity;

import com.portfolio.auctionmarket.global.base.Base;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "seller_status")
    private String sellerStatus;

    @Column(name = "point")
    private Long point;

    @Column(name = "avg_rating")
    private Double avgRating;

    public void subPoint(Long point) {
        if (this.point < point) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINTS);
        }
        this.point -= point;
    }

    public void addPoint(Long point) {
        this.point += point;
    }
}
