package com.portfolio.auctionmarket.domain.ratings.repository;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByOrder(Order order);
}
