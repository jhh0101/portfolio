package com.portfolio.auctionmarket.domain.products.service;

import com.portfolio.auctionmarket.domain.products.dto.ProductImageResponse;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.repository.ProductImageRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageRepository productImageRepository;
    private final S3Service s3Service;

    @Transactional
    public List<ProductImageResponse> uploadImages(Long productId, List<MultipartFile> files) {
        List<ProductImageResponse> responses = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            int order = i + 1; // 0번 인덱스는 1번, 1번 인덱스는 2번...
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
