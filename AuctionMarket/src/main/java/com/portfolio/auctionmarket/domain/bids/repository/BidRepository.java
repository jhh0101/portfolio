package com.portfolio.auctionmarket.domain.bids.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
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

    @Query("SELECT b " +
            "FROM Bid b " +
            "JOIN FETCH b.bidder " +
            "JOIN b.auction a " +
            "JOIN a.product p " +
            "JOIN p.seller s " +
            "WHERE s.userId = :sellerId " +
            "AND b.bidPrice = (" +
            "SELECT MAX(b2.bidPrice) " +
            "FROM Bid b2 " +
            "WHERE b2.auction = b.auction) " +
            "AND a.status = 'PROCEEDING'")
    List<Bid> findTopBidsPerAuctionBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT b FROM Bid b " +
            "WHERE b.bidder.userId = :userId " +
            "AND b.bidId IN (" +
            "   SELECT MAX(b2.bidId) FROM Bid b2 " +
            "   WHERE b2.bidder.userId = :userId " +
            "   GROUP BY b2.auction" +
            ")")
    List<Bid> findLatestBidsByUserId(@Param("userId") Long userId);

    Optional<Bid> findTopByAuctionOrderByBidPriceDesc(Auction auction);

    @Query("SELECT COUNT(b) " +
            "FROM Bid b " +
            "JOIN b.auction a " +
            "WHERE a.currentPrice = b.bidPrice " +
            "AND a.status = 'PROCEEDING' " +
            "AND b.bidder.userId = :bidderId")
    Long bidCount(@Param("bidderId") Long bidderId);

    @Query(value = "SELECT b.bidId AS bidId, b.auction.auctionId AS auctionId, u.nickname AS nickname, b.bidPrice AS bidPrice " +
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
