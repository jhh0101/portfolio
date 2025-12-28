package com.portfolio.auctionmarket.domain.auctions.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
