package com.portfolio.auctionmarket.domain.products.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.products.dto.ProductDetailAndAuctionResponse;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByCategory_CategoryId(Long categoryId);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.auction a " +
            "WHERE p.productId = :productId")
    Optional<Product> findWithAuctionById(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.productId = :productId")
    Integer viewCount(@Param("productId") Long productId);
}
