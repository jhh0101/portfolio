package com.portfolio.auctionmarket.domain.sellers.repository;

import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUser_UserId(Long userId);
}
