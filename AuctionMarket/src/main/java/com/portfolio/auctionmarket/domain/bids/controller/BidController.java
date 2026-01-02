package com.portfolio.auctionmarket.domain.bids.controller;

import com.portfolio.auctionmarket.domain.bids.dto.BidCancelResponse;
import com.portfolio.auctionmarket.domain.bids.dto.BidRequest;
import com.portfolio.auctionmarket.domain.bids.dto.BidResponse;
import com.portfolio.auctionmarket.domain.bids.service.BidService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bid")
public class BidController {

    private final BidService bidService;

    @PostMapping
    public ResponseEntity<ApiResponse<BidResponse>> addBid(@Valid @RequestBody BidRequest request,
                                                           @AuthenticationPrincipal Long userId) {
        BidResponse response = bidService.addBid(userId, request);
        return ResponseEntity.ok(ApiResponse.success("입찰 성공", response));
    }

    @PostMapping("/{bidId}")
    public ResponseEntity<ApiResponse<BidCancelResponse>> cancelBid(@AuthenticationPrincipal Long userId,
                                                                    @PathVariable Long bidId) {
        BidCancelResponse response = bidService.cancelBid(userId, bidId);
        return ResponseEntity.ok(ApiResponse.success("입찰 취소", response));
    }
}
