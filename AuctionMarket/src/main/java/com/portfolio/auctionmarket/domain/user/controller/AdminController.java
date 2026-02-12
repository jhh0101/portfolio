package com.portfolio.auctionmarket.domain.user.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.user.dto.*;
import com.portfolio.auctionmarket.domain.user.service.AdminService;
import com.portfolio.auctionmarket.domain.user.service.UserService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @PostMapping("/{userId}/suspend")
    public ResponseEntity<ApiResponse<UserDeleteResponse>> suspend(@PathVariable Long userId, @RequestBody UserSuspensionRequest request) {
        UserDeleteResponse response = adminService.suspend(userId, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정지", response));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> userList(UserListCondition condition, Pageable pageable) {
        Page<UserResponse> responses = adminService.userList(condition, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 리스트 조회", responses));
    }

    @GetMapping("/suspension-status/{userId}")
    public ResponseEntity<ApiResponse<WithdrawalStatusResponse>> suspensionStatus(@PathVariable Long userId) {
        WithdrawalStatusResponse response = userService.withdrawalStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 상태 조회", response));
    }

    @GetMapping("/suspension-reason/{userId}")
    public ResponseEntity<ApiResponse<UserSuspendReasonResponse>> suspensionReason(@PathVariable Long userId) {
        UserSuspendReasonResponse response = adminService.suspendReason(userId);
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 상태 조회", response));
    }

}
