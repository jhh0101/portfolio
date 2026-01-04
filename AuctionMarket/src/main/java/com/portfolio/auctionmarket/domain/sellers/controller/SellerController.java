package com.portfolio.auctionmarket.domain.sellers.controller;

import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerResponse;
import com.portfolio.auctionmarket.domain.sellers.service.SellerService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
