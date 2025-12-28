package com.portfolio.auctionmarket.domain.auctions.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRequest {

    @NotNull(message = "시작가를 입력해주세요.")
    private Integer startPrice;

    @NotNull(message = "시작 기간을 입력해주세요.")
    private LocalDateTime startTime;

    @NotNull(message = "종료 기간을 입력해주세요.")
    private LocalDateTime endTime;

}
