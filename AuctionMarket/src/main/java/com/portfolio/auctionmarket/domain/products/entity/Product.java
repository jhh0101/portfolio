package com.portfolio.auctionmarket.domain.products.entity;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.base.BaseCreatedAt;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "products")
@Builder
public class Product extends BaseCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private Auction auction;

}
