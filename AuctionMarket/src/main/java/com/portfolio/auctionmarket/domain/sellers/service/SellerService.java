package com.portfolio.auctionmarket.domain.sellers.service;

import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerResponse;
import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.sellers.repository.SellerRepository;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Transactional
    public SellerResponse sellerApply(Long userId, SellerApplyRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Optional<Seller> optionalSeller = sellerRepository.findByUser_UserId(userId);
        Seller seller;

        if (optionalSeller.isPresent()) {
            seller = optionalSeller.get();
            if (seller.getStatus() != SellerStatus.REJECTED) {
                throw new CustomException(ErrorCode.DUPLICATE_SELLER, "이미 신청 중...");
            }
            seller.updateApply(user, request);
        } else {
            seller = Seller.builder()
                    .user(user)
                    .storeName(request.getStoreName())
                    .bankName(request.getBankName())
                    .accountNumber(request.getAccountNumber())
                    .accountHolder(request.getAccountHolder())
                    .status(SellerStatus.PENDING)
                    .build();
        }

        sellerRepository.save(seller);

        return SellerResponse.from(seller);
    }

}
