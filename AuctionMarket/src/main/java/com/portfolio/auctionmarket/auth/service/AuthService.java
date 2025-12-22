package com.portfolio.auctionmarket.auth.service;

import com.portfolio.auctionmarket.auth.dto.LoginRequest;
import com.portfolio.auctionmarket.auth.dto.TokenResponse;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.config.JwtProperties;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.Error;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getUserId());

        refreshTokenService.saveRefreshTokenBidirectional(user.getUserId(), refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .tokenType("Bearer")
                .build();
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        jwtService.validateToken(refreshToken);

        Long userId = refreshTokenService.getUserIdByToken(refreshToken);
        if (userId == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtService.generateAccessToken(user.getUserId(), user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUserId());

        refreshTokenService.saveRefreshTokenBidirectional(user.getUserId(), newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    public void logout(Long userId) {
        refreshTokenService.deleteRefreshToken(userId);
    }
}
