package com.portfolio.auctionmarket.domain.products.service;

import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.repository.ProductImageRepository;
import com.portfolio.auctionmarket.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageRepository productImageRepository;
    private final S3Service s3Service;

    public void uploadImages(Long productId, List<MultipartFile> files) {
        for (int i = 0; i < files.size(); i++) {
            int order = i + 1; // 0번 인덱스는 1번, 1번 인덱스는 2번...
            String url = s3Service.upload(files.get(i));

            ProductImage img = ProductImage.builder()
                    .productId(productId)
                    .imageUrl(url)
                    .imageOrder(order) // 여기서 상품별 순번 결정!
                    .build();

            productImageRepository.save(img);
        }
    }
}
