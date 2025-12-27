package com.portfolio.auctionmarket.domain.products.service;

import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.categories.repository.CategoryRepository;
import com.portfolio.auctionmarket.domain.products.dto.ProductImageResponse;
import com.portfolio.auctionmarket.domain.products.dto.ProductRequest;
import com.portfolio.auctionmarket.domain.products.dto.ProductResponse;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.entity.Status;
import com.portfolio.auctionmarket.domain.products.repository.ProductImageRepository;
import com.portfolio.auctionmarket.domain.products.repository.ProductRepository;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 상품 메서드
    @Transactional
    public ProductResponse addProduct(Long userId, ProductRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .seller(user)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(Status.ACTIVE)
                .build();

        Product productSave = productRepository.save(product);

        return ProductResponse.from(productSave);

    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> productList(String title, String path, Pageable pageable) {
        Page<Product> product = productRepository.findByTitleAndCategory(title, path, pageable);

        return product.map(ProductResponse::from);
    }


    // 이미지 메서드
    @Transactional
    public List<ProductImageResponse> uploadImages(Long productId, List<MultipartFile> files) {
        List<ProductImageResponse> responses = new ArrayList<>();

        Integer lastOrder = productImageRepository.findMaxOrderByProductId(productId);

        for (int i = 0; i < files.size(); i++) {
            int order = lastOrder + i + 1; // 0번 인덱스는 1번, 1번 인덱스는 2번...
            String url = s3Service.uploadFile(files.get(i), "products");

            ProductImage img = ProductImage.builder()
                    .productId(productId)
                    .imageUrl(url)
                    .imageOrder(order) // 여기서 상품별 순번 결정!
                    .build();

            ProductImage savedImg = productImageRepository.save(img);

            // 생성된 정보를 DTO에 담아 리스트에 추가
            responses.add(ProductImageResponse.from(savedImg));
        }
        return responses;
    }

    @Transactional
    public void moveToMain(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        Integer oldOrder = image.getImageOrder();
        Integer newOrder = 1;

        productImageRepository.shiftOrders(image.getProductId(), newOrder, oldOrder);

        image.updateOrder(newOrder);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        s3Service.deleteFile(image.getImageUrl());
        productImageRepository.delete(image);

        List<ProductImage> remainingImages = productImageRepository.findByProductIdOrderByImageOrderAsc(image.getProductId() );

        for (int i = 0; i < remainingImages.size(); i++) {
            remainingImages.get(i).updateOrder(i + 1); // 엔티티에 updateOrder 메서드 필요
        }
    }
}
