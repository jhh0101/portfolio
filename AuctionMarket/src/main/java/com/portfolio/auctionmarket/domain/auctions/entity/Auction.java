package com.portfolio.auctionmarket.domain.auctions.entity;

import com.portfolio.auctionmarket.domain.products.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "UPDATE auctions SET status = 'CANCELED' WHERE auction_id = ?")
@SQLRestriction("status != 'CANCELED' AND status != 'ENDED'")
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

    public void updateAuction(Long startPrice, LocalDateTime startTime, LocalDateTime endTime) {
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void changeStatus(AuctionStatus status) {
        this.status = status;
    }

}
