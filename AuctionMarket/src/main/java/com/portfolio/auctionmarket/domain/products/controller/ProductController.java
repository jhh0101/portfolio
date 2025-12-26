package com.portfolio.auctionmarket.domain.products.controller;

import com.portfolio.auctionmarket.domain.products.dto.ProductImageResponse;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.service.ProductService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> uploadImages(@PathVariable("id") Long productId, // 경로에서 상품 ID 추출
                                                                                @RequestPart("files") List<MultipartFile> files){
        List<ProductImageResponse> response = productService.uploadImages(productId, files);
        return ResponseEntity.ok(ApiResponse.success("이미지 업로드", response));
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable("id") Long imageId) {
        productService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("이미지 삭제", null));
    }

}
