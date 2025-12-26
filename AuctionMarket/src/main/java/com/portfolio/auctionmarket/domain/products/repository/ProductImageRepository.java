package com.portfolio.auctionmarket.domain.products.repository;

import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
