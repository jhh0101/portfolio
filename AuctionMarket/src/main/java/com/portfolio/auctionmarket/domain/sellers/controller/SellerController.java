package com.portfolio.auctionmarket.domain.sellers.controller;

import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerRejectRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerResponse;
import com.portfolio.auctionmarket.domain.sellers.service.SellerService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/me/seller")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerApply(@AuthenticationPrincipal Long userId,
                                                                   @Valid @RequestBody SellerApplyRequest request) {
        SellerResponse response = sellerService.sellerApply(userId, request);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 신청", response));
    }

    @PatchMapping("/{sellerId}/cancel")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerCancel(@AuthenticationPrincipal Long userId,
                                                                   @PathVariable Long sellerId) {
        SellerResponse response = sellerService.sellerCancel(sellerId, userId);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 취소", response));
    }

    @PatchMapping("/{sellerId}/approve")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerApprove(@PathVariable Long sellerId) {
        SellerResponse response = sellerService.approveSeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 승인", response));
    }

    @PatchMapping("/{sellerId}/reject")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerReject(@PathVariable Long sellerId,
                                                                    @Valid @RequestBody SellerRejectRequest request) {
        SellerResponse response = sellerService.rejectSeller(sellerId, request);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 승인", response));
    }
}
