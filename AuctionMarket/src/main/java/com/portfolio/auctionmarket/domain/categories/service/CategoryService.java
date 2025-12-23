package com.portfolio.auctionmarket.domain.categories.service;

import com.portfolio.auctionmarket.domain.categories.dto.CategoryRequest;
import com.portfolio.auctionmarket.domain.categories.dto.CategoryResponse;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.categories.repository.CategoryRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse addCategory(CategoryRequest request) {
        Category parent = null;
        String path;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Category categorySave = Category.builder()
                .category(request.getCategory())
                .parent(parent)
                .build();

        Category category = categoryRepository.save(categorySave);

        if (parent == null) {
            path = String.valueOf(category.getCategoryId());
        } else {
            path = parent.getPath() + "/" + category.getCategoryId();
            parent.addChildrenCategory(category);
        }
        category.setPath(path);

        return CategoryResponse.from(category);
    }
}
