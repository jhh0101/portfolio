package com.portfolio.auctionmarket.domain.categories.controller;

import com.portfolio.auctionmarket.domain.categories.dto.CategoryRequest;
import com.portfolio.auctionmarket.domain.categories.dto.CategoryResponse;
import com.portfolio.auctionmarket.domain.categories.service.CategoryService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 생성", response));
    }
}
