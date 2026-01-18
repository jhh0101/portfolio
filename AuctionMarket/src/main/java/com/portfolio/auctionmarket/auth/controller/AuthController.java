package com.portfolio.auctionmarket.auth.controller;

import com.portfolio.auctionmarket.auth.dto.LoginRequest;
import com.portfolio.auctionmarket.auth.dto.TokenResponse;
import com.portfolio.auctionmarket.auth.service.AuthService;
import com.portfolio.auctionmarket.global.error.LoginException;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader("Set-Cookie", responseCookie.toString());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenResponse.getAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }

}
