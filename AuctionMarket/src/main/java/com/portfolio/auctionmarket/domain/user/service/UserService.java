package com.portfolio.auctionmarket.domain.user.service;

import com.portfolio.auctionmarket.domain.user.dto.UserResponse;
import com.portfolio.auctionmarket.domain.user.dto.UserSingupRequest;
import com.portfolio.auctionmarket.domain.user.dto.UserSuspensionRequest;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.entity.UserStatus;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.util.MaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(UserSingupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .nickname(request.getNickname())
            .phone(request.getPhone())
            .password(passwordEncoder.encode(request.getPassword()))
            .point(0L)
            .avgRating(0.0)
            .sellerStatus(SellerStatus.NONE)
            .status(UserStatus.NORMAL)
            .role(Role.USER)
            .build();

        User saveUser = userRepository.save(user);
        log.info("User created: userId={}, email={}, nickname={}", saveUser.getUserId(), saveUser.getEmail(), saveUser.getNickname());

        return UserResponse.from(saveUser);
    }

    @Transactional
    public void withdrawn(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String formatPhone = MaskingUtil.formatPhone(user.getPhone());
        String maskEmail = MaskingUtil.maskEmail(user.getEmail());
        String maskUsername = MaskingUtil.maskUsername(user.getUsername());
        String maskPhone = MaskingUtil.maskPhone(formatPhone);

        user.withdraw(maskEmail, maskUsername, maskPhone, userId);
    }

    @Transactional
    public void suspend(Long userId, UserSuspensionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        user.suspend(userId, request.getSuspensionReason());
    }
}
