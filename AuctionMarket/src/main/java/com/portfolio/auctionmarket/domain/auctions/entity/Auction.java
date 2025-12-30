package com.portfolio.auctionmarket.domain.auctions.entity;

import com.portfolio.auctionmarket.domain.products.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private Long auctionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "start_price")
    private Long startPrice;

    @Column(name = "current_price")
    private Long currentPrice;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    public void updateCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

}
