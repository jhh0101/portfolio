package com.portfolio.auctionmarket.domain.products.service;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionRequest;
import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.categories.repository.CategoryRepository;
import com.portfolio.auctionmarket.domain.products.dto.*;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.domain.products.repository.ProductImageRepository;
import com.portfolio.auctionmarket.domain.products.repository.ProductRepository;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final S3Service s3Service;
    private final RedissonClient redissonClient;

    // 상품 메서드
    @Transactional
    public ProductResponse addProduct(Long userId, ProductRequest productRequest, AuctionRequest auctionRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .seller(user)
                .category(category)
                .title(productRequest.getTitle())
                .description(productRequest.getDescription())
                .productStatus(ProductStatus.ACTIVE)
                .build();

        Product productSave = productRepository.save(product);

        Auction auction = Auction.builder()
                .product(productSave)
                .startPrice(auctionRequest.getStartPrice())
                .currentPrice(auctionRequest.getStartPrice())
                .startTime(auctionRequest.getStartTime())
                .endTime(auctionRequest.getEndTime())
                .status(AuctionStatus.PROCEEDING)
                .build();

        Auction auctionSave = auctionRepository.save(auction);


        RScoredSortedSet<Long> closingQueue = redissonClient.getScoredSortedSet("auction:closing");

        Long closingTimestamp = auctionSave.getEndTime()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();

        closingQueue.add(closingTimestamp, auctionSave.getAuctionId());

        return ProductResponse.from(productSave);

    }

    @Transactional(readOnly = true)
    public Page<ProductAndAuctionResponse> productList(String title, String path, Pageable pageable) {
        Page<Product> auctions;
        if (path != null && !path.isEmpty()) {
            auctions = productRepository.findByTitleAndCategory(title, path, pageable);
        } else {
            auctions = productRepository.findByTitle(title, pageable);
        }
        return auctions.map(ProductAndAuctionResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductDetailAndAuctionResponse findProductDetail(Long productId) {
        Product product = productRepository.findWithAuctionById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다."));
        return ProductDetailAndAuctionResponse.from(product);
    }

    @Transactional
    public ProductDetailAndAuctionResponse updateProductDetail(Long userId, Long productId, ProductRequest productRequest, AuctionRequest auctionRequest) {

        Product product = productRepository.findWithAuctionById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다."));

        if (!userId.equals(product.getSeller().getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "사용자가 일치하지 않습니다.");
        }

        if (bidRepository.existsByAuction(product.getAuction())) {
            throw new CustomException(ErrorCode.CANNOT_MODIFY_AFTER_BID, "입찰한 상품은 수정할 수 없습니다.");
        }

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        product.updateProduct(
                category,
                productRequest.getTitle(),
                productRequest.getDescription(),
                auctionRequest.getStartPrice(),
                auctionRequest.getStartTime(),
                auctionRequest.getEndTime()
        );

        RScoredSortedSet<Long> closingQueue = redissonClient.getScoredSortedSet("auction:closing");

        long newScore = auctionRequest.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond();

        closingQueue.add(newScore, product.getAuction().getAuctionId());

        return ProductDetailAndAuctionResponse.from(product);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long userId, Long productId) {

        Product product = productRepository.findWithAuctionById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다."));

        if (!userId.equals(product.getSeller().getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "사용자가 일치하지 않습니다.");
        }

        if (bidRepository.existsByAuction(product.getAuction())) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_AFTER_BID, "입찰한 상품은 삭제할 수 없습니다.");
        }
        product.getAuction().changeStatus(AuctionStatus.CANCELED);
        productRepository.delete(product);
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
