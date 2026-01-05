package com.portfolio.auctionmarket.domain.orders.repository;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
