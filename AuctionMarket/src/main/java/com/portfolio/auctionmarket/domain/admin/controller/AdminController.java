package com.portfolio.auctionmarket.domain.admin.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.bids.dto.BidHistoryResponse;
import com.portfolio.auctionmarket.domain.bids.dto.BidResponseImpl;
import com.portfolio.auctionmarket.domain.bids.service.BidAdminService;
import com.portfolio.auctionmarket.domain.bids.service.BidService;
import com.portfolio.auctionmarket.domain.user.dto.*;
import com.portfolio.auctionmarket.domain.user.service.UserAdminService;
import com.portfolio.auctionmarket.domain.user.service.UserService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserAdminService userAdminService;
    private final UserService userService;
    private final BidAdminService bidAdminService;

    @PostMapping("/{userId}/suspend")
    public ResponseEntity<ApiResponse<UserDeleteResponse>> suspend(@PathVariable Long userId, @RequestBody UserSuspensionRequest request) {
        UserDeleteResponse response = userAdminService.suspend(userId, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정지", response));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> userList(UserListCondition condition, Pageable pageable) {
        Page<UserResponse> responses = userAdminService.userList(condition, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 리스트 조회", responses));
    }

    @GetMapping("/suspension-status/{userId}")
    public ResponseEntity<ApiResponse<WithdrawalStatusResponse>> suspensionStatus(@PathVariable Long userId) {
        WithdrawalStatusResponse response = userService.withdrawalStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 상태 조회", response));
    }

    @GetMapping("/suspension-reason/{userId}")
    public ResponseEntity<ApiResponse<UserSuspendReasonResponse>> suspensionReason(@PathVariable Long userId) {
        UserSuspendReasonResponse response = userAdminService.suspendReason(userId);
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 상태 조회", response));
    }

    @GetMapping("/{userId}/bid")
    public ResponseEntity<ApiResponse<Slice<BidHistoryResponse>>> findBidHistory(@PathVariable Long userId,
                                                                                 @PageableDefault(size = 10) Pageable pageable){
        Slice<BidHistoryResponse> responses = bidAdminService.findBidHistory(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("입찰 상품 리스트 출력", responses));
    }

    @GetMapping("/{auctionId}/bid-list/{userId}")
    public ResponseEntity<ApiResponse<Slice<BidResponseImpl>>> findUserBidList(@PathVariable Long userId,
                                                                               @PathVariable Long auctionId,
                                                                               @PageableDefault(size = 10) Pageable pageable){
        Slice<BidResponseImpl> responses = bidAdminService.findUserBidList(userId, auctionId, pageable);
        return ResponseEntity.ok(ApiResponse.success("입찰 상품 리스트 출력", responses));
    }

}
