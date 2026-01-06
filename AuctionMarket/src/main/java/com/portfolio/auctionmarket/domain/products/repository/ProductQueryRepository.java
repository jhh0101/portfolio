package com.portfolio.auctionmarket.domain.products.repository;


import com.portfolio.auctionmarket.domain.products.dto.ProductListCondition;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.portfolio.auctionmarket.domain.auctions.entity.QAuction.auction;
import static com.portfolio.auctionmarket.domain.bids.entity.QBid.bid;
import static com.portfolio.auctionmarket.domain.products.entity.QProduct.product;
import static com.portfolio.auctionmarket.domain.categories.entity.QCategory.category1;
import static com.portfolio.auctionmarket.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;;

    public Page<Product> productList(ProductListCondition condition, Pageable pageable) {
        List<Product> content = jpaQueryFactory
                .selectFrom(product)
                .innerJoin(product.seller, user).fetchJoin()
                .innerJoin(product.auction, auction).fetchJoin()
                .leftJoin(product.category, category1).fetchJoin()
                .where(
                        isMyAuction(condition.userId()),
                        titleContain(condition.title()),
                        pathStartWith(condition.path()),
                        statusFilter(condition.userId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier(condition.sort()))
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category1)
                .where(
                        isMyAuction(condition.userId()),
                        titleContain(condition.title()),
                        pathStartWith(condition.path()),
                        statusFilter(condition.userId())
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression isMyAuction(Long userId) {
        return userId != null ? product.seller.userId.eq(userId) : null;
    }
    private BooleanExpression titleContain(String title) {
        return StringUtils.hasText(title) ? product.title.contains(title) : null;
    }
    private BooleanExpression pathStartWith(String path) {
        return StringUtils.hasText(path) ? product.category.path.startsWith(path + "/") : null;
    }

    private BooleanExpression statusFilter(Long userId) {
        if (userId == null) {
            return null;
        }
        return product.productStatus.in(ProductStatus.SOLD, ProductStatus.FAILED);
    }

    private OrderSpecifier<?> orderSpecifier(String sort) {
        if ("endingSoon".equals(sort)) {
            return product.auction.endTime.asc();
        } else if ("priceHigh".equals(sort)) {
            return product.auction.currentPrice.desc();
        } else if ("priceLow".equals(sort)) {
            return product.auction.currentPrice.asc();
        }
        return product.productId.desc();
    }
}
