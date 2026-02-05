package com.portfolio.auctionmarket.domain.bids.repository;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionResponse;
import com.portfolio.auctionmarket.domain.auctions.entity.QAuction;
import com.portfolio.auctionmarket.domain.bids.dto.BidHistoryResponse;
import com.portfolio.auctionmarket.domain.bids.entity.QBid;
import com.portfolio.auctionmarket.domain.products.dto.ProductAndAuctionResponse;
import com.portfolio.auctionmarket.domain.products.dto.ProductResponse;
import com.portfolio.auctionmarket.domain.products.entity.QProduct;
import com.portfolio.auctionmarket.domain.products.entity.QProductImage;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidQueryRepository {

    QBid bid = QBid.bid;
    QAuction auction = QAuction.auction;
    QProduct product = QProduct.product;
    QProductImage productImage = QProductImage.productImage;

    private final JPAQueryFactory jpaQueryFactory;

    public Page<BidHistoryResponse> findBidHistory(Long userId, Pageable pageable) {
        List<BidHistoryResponse> content = jpaQueryFactory
                .select(Projections.constructor(BidHistoryResponse.class,
                        Projections.constructor(ProductAndAuctionResponse.class,
                                Projections.constructor(ProductResponse.class,
                                        product.productId,
                                        product.seller.nickname,
                                        product.category.category,
                                        product.title,
                                        product.productStatus,

                                        JPAExpressions
                                                .select(productImage.imageUrl)
                                                .from(productImage)
                                                .where(
                                                        productImage.product.eq(product),
                                                        productImage.imageOrder.eq(1)
                                                ),
                                        product.createdAt
                                ),
                                Projections.constructor(AuctionResponse.class,
                                        auction.auctionId,
                                        auction.startPrice,
                                        auction.currentPrice,
                                        auction.startTime,
                                        auction.endTime,
                                        auction.status
                                )
                        ),
                        bid.bidPrice.max() // 내 최고 입찰가
                ))
                .from(bid)
                .join(bid.auction, auction)
                .join(auction.product, product)
                .where(bid.bidder.userId.eq(userId))
                .groupBy(auction.auctionId)

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bid.createdAt.max().desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(auction.countDistinct())
                .from(bid)
                .join(bid.auction, auction)
                .where(bid.bidder.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
