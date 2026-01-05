package com.portfolio.auctionmarket.domain.auctions.service;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.orders.dto.OrderResponse;
import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.orders.repository.OrderRepository;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Optional<OrderResponse> finishAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND, "옥션을 찾을 수 없습니다."));

        if (auction.getStatus() != AuctionStatus.PROCEEDING) {
            return Optional.empty();
        }

        Optional<Bid> topBid = bidRepository.findTopByAuctionOrderByBidIdDesc(auction);
        if (topBid.isPresent()) {
            Bid winnerBid = topBid.get();
            Order order = Order.builder()
                .auction(auction)
                .buyer(winnerBid.getBidder())
                .finalPrice(winnerBid.getBidPrice())
                .build();
            orderRepository.save(order);
            auction.getProduct().changeStatus(ProductStatus.SOLD);
            log.info("경매 낙찰 완료 - ID: {}", auctionId);
            return Optional.of(OrderResponse.from(order));
        } else {
            auction.getProduct().changeStatus(ProductStatus.FAILED);
            log.info("경매 유찰 완료 (입찰자 없음) - ID: {}", auctionId);
        }
        auction.changeStatus(AuctionStatus.ENDED);

        return Optional.empty();
    }
}
