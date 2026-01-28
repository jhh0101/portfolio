package com.portfolio.auctionmarket.domain.bids.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.bids.dto.BidResponse;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Boolean existsByAuction(Auction auction);

    Optional<Bid> findTopByAuctionOrderByBidIdDesc(Auction auction);

    List<Bid> findAllByAuctionOrderByBidPriceDesc(Auction auction);

    @Query(value = "SELECT b.bidId AS bidId, u.nickname AS nickname, b.bidPrice AS bidPrice " +
            "FROM Bid b " +
            "LEFT JOIN User u ON b.bidder.userId = u.userId " +
            "WHERE b.auction.auctionId = :auctionId",

            countQuery = "SELECT COUNT(*) FROM Bid b WHERE b.auction.auctionId = :auctionId")
    Page<BidResponse> findAllByAuction_AuctionId(@Param("auctionId") Long auctionId, Pageable pageable);

    @Query(value = "SELECT b.bid_id AS bidId, u.nickname AS nickname, b.bid_price AS bidPrice " +
            "FROM bids b " +
            "LEFT JOIN users u ON b.bidder_id = u.user_id " +
            "WHERE b.auction_id = :auctionId",

            countQuery = "SELECT COUNT(*) FROM bids b WHERE b.auction_id = :auctionId",
            nativeQuery = true)
    Page<BidResponse> findAllByAuction_AuctionIdToSeller(@Param("auctionId") Long auctionId, Pageable pageable);
}
