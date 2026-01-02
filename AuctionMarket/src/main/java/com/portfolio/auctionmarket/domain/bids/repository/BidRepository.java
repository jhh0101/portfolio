package com.portfolio.auctionmarket.domain.bids.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Boolean existsByAuction(Auction auction);

    Optional<Bid> findTopByAuctionOrderByBidIdDesc(Auction auction);

    List<Bid> findAllByAuctionOrderByBidPriceDesc(Auction auction);
}
