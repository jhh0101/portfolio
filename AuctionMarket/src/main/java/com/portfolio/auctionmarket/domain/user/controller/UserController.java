package com.portfolio.auctionmarket.domain.user.controller;

import com.portfolio.auctionmarket.domain.user.dto.*;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.service.UserService;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody UserSingupRequest request){
        UserResponse userResponse = userService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공", userResponse));
    }

    @PostMapping("/{userId}/withdrawn")
    public ResponseEntity<ApiResponse<UserDeleteResponse>> withdrawn(@PathVariable Long userId, @RequestBody UserWithdrawnRequest request) {
        UserDeleteResponse response = userService.withdrawn(userId, request);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴", response));
    }

    @PostMapping("/{userId}/suspend")
    public ResponseEntity<ApiResponse<UserDeleteResponse>> suspend(@PathVariable Long userId, @RequestBody UserSuspensionRequest request) {
        UserDeleteResponse response = userService.suspend(userId, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정지", response));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> userList(UserListCondition condition, Pageable pageable) {
        Page<UserResponse> responses = userService.userList(condition, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 리스트 조회", responses));
    }

}
