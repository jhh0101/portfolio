package com.portfolio.auctionmarket.domain.orders.repository;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"auction", "auction.product"})
    Page<Order> findAllByBuyer_UserId(Long userId, Pageable pageable);

    Order findAllByAuction_AuctionId(Long auctionAuctionId);
}
