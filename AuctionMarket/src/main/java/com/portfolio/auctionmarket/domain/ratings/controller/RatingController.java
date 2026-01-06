package com.portfolio.auctionmarket.domain.ratings.controller;

import com.portfolio.auctionmarket.domain.ratings.dto.RatingRequest;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingResponse;
import com.portfolio.auctionmarket.domain.ratings.service.RatingService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<RatingResponse>> createRating(@AuthenticationPrincipal Long userId,
                                                                    @PathVariable Long orderId,
                                                                    @RequestBody RatingRequest request) {
        RatingResponse response = ratingService.createRating(userId, orderId, request);
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 등록", response));
    }
}
