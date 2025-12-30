package com.portfolio.auctionmarket.domain.products.repository;

import com.portfolio.auctionmarket.domain.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.category c " + // 객체 그래프 탐색 방식 권장
            "WHERE p.title LIKE CONCAT('%', :title, '%') " +
            "AND c.path LIKE CONCAT(:path, '/%')")
    Page<Product> findByTitleAndCategory(@Param("title") String title, @Param("path") String path, Pageable pageable);


    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.category c " + // 객체 그래프 탐색 방식 권장
            "WHERE p.title LIKE CONCAT('%', :title, '%')")
    Page<Product> findByTitle(@Param("title") String title, Pageable pageable);

    Product findByCategory_CategoryId(Long categoryId);
}
