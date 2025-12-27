package com.portfolio.auctionmarket.domain.products.controller;

import com.portfolio.auctionmarket.domain.products.dto.ProductImageResponse;
import com.portfolio.auctionmarket.domain.products.dto.ProductRequest;
import com.portfolio.auctionmarket.domain.products.dto.ProductResponse;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.service.ProductService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 메서드
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductRequest request,
                                                                   @AuthenticationPrincipal Long userId) {
        ProductResponse response = productService.addProduct(userId, request);
        return ResponseEntity.ok(ApiResponse.success("상품 등록", response));
    }

    // 이미지 메서드
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> uploadImages(@PathVariable("id") Long productId, // 경로에서 상품 ID 추출
                                                                                @RequestPart("files") List<MultipartFile> files){
        List<ProductImageResponse> response = productService.uploadImages(productId, files);
        return ResponseEntity.ok(ApiResponse.success("이미지 업로드", response));
    }

    @PatchMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> moveToMain(@PathVariable("id") Long id) {
        productService.moveToMain(id);
        return ResponseEntity.ok(ApiResponse.success("이미지 메인 변경", null));
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable("id") Long imageId) {
        productService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("이미지 삭제", null));
    }

}
