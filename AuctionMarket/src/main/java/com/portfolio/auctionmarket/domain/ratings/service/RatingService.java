package com.portfolio.auctionmarket.domain.ratings.service;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.orders.repository.OrderQueryRepository;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingRequest;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingResponse;
import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import com.portfolio.auctionmarket.domain.ratings.repository.RatingRepository;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final UserRepository userRepository;

    @Transactional
    public RatingResponse createRating(Long userId, Long orderId, RatingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Order order = orderQueryRepository.findOrderWithProductAndSeller(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND, "주문 내역을 찾을 수 없습니다."));

        if (!order.getBuyer().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "구매자가 일치하지 않습니다.");
        }

        Rating rating = Rating.builder()
                .order(order)
                .toUser(order.getSeller())
                .fromUser(user)
                .score(request.getScore())
                .comment(request.getComment())
                .build();

        Rating ratingSave = ratingRepository.save(rating);

        return RatingResponse.from(ratingSave);
    }
}
