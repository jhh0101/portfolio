package com.portfolio.auctionmarket.domain.user.service;

import com.portfolio.auctionmarket.domain.user.dto.UserResponse;
import com.portfolio.auctionmarket.domain.user.dto.UserSingupRequest;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
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
            .password(passwordEncoder.encode(request.getPassword()))
            .point(0L)
            .avgRating(0.0)
            .role(Role.USER)
            .build();

        User saveUser = userRepository.save(user);
        log.info("User created: userId={}, email={}, nickname={}", saveUser.getUserId(), saveUser.getEmail(), saveUser.getNickname());

        return UserResponse.from(saveUser);
    }
}
