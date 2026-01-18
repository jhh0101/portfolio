LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'생활용품',NULL,'1', 0)
                              ,(2,'가전제품',NULL,'2', 0)
                              ,(3,'식품',NULL,'3', 0)
                              ,(4,'PC/노트북',2,'2/4', 0)
                              ,(5,'LG 노트북',4,'2/4/5', 0)
                              ,(6,'삼성 노트북',4,'2/4/6', 0)
                              ,(7,'HP 노트북',4,'2/4/7', 0)
                              ,(8,'ASUS 노트북',4,'2/4/8', 0)
                              ,(9,'삼성 PC',4,'2/4/9', 0)
                              ,(10,'LG PC',4,'2/4/10', 0)
                              ,(11,'세탁기',2,'2/11', 0)
                              ,(12,'냉장고',2,'2/12', 0)
                              ,(13,'에어컨',2,'2/13', 0)
                              ,(14,'통돌이 세탁기',11,'2/11/14', 0)
                              ,(15,'LG 세탁기',11,'2/11/15', 0)
                              ,(16,'삼성 세탁기',11,'2/11/16', 0)

/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

-- 1. 외래 키 체크 해제 및 기존 테이블 삭제 (초기화용)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `auctions`;
DROP TABLE IF EXISTS `product_images`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `users`;

-- 2. 테이블 생성 (Schema)

-- 유저 테이블
CREATE TABLE `users` (
                         `user_id` bigint NOT NULL AUTO_INCREMENT,
                         `email` varchar(100) NOT NULL,
                         `password` varchar(255) DEFAULT NULL,
                         `nickname` varchar(20) NOT NULL,
                         `username` varchar(20) NOT NULL,
                         `phone` varchar(15) NOT NULL,
                         `role` enum('USER','SELLER','ADMIN') DEFAULT 'USER',
                         `point` bigint NOT NULL DEFAULT '0',
                         `avg_rating` double NOT NULL DEFAULT '0',
                         `status` enum('NORMAL','SUSPENDED','WITHDRAWN') DEFAULT 'NORMAL',
                         `suspension_reason` varchar(255) DEFAULT NULL,
                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`user_id`),
                         UNIQUE KEY `email` (`email`),
                         UNIQUE KEY `nickname` (`nickname`),
                         UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 상품 테이블 (주의: categories 테이블이 먼저 존재해야 합니다)
CREATE TABLE `products` (
                            `product_id` bigint NOT NULL AUTO_INCREMENT,
                            `seller_id` bigint NOT NULL,
                            `category_id` bigint NOT NULL,
                            `title` varchar(200) NOT NULL,
                            `description` text,
                            `status` enum('ACTIVE','SOLD','FAILED','DELETED') DEFAULT 'ACTIVE',
                            `view_count` int DEFAULT '0',
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`product_id`),
                            CONSTRAINT `products_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`),
                            CONSTRAINT `products_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 상품 이미지 테이블
CREATE TABLE `product_images` (
                                  `image_id` bigint NOT NULL AUTO_INCREMENT,
                                  `product_id` bigint NOT NULL,
                                  `image_url` varchar(500) NOT NULL,
                                  `image_order` int DEFAULT '1',
                                  PRIMARY KEY (`image_id`),
                                  CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 경매 테이블
CREATE TABLE `auctions` (
                            `auction_id` bigint NOT NULL AUTO_INCREMENT,
                            `product_id` bigint NOT NULL,
                            `start_price` bigint NOT NULL,
                            `current_price` bigint NOT NULL,
                            `start_time` datetime NOT NULL,
                            `end_time` datetime NOT NULL,
                            `status` varchar(20) DEFAULT 'PROCEEDING',
                            PRIMARY KEY (`auction_id`),
                            UNIQUE KEY `product_id` (`product_id`),
                            CONSTRAINT `auctions_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 3. 데이터 삽입 (Data)

-- 유저 데이터
INSERT INTO `users` (`user_id`, `email`, `password`, `nickname`, `username`, `phone`, `role`, `status`, `created_at`, `updated_at`)
VALUES (1, 'gogns0101@naver.com', '$2a$10$7aHIt7dWXim8vv14jWHUAu.ZVxyFllAPNK46COutG2/5wcGm7D/hu', 'gogns', 'gogns', '01012345678', 'SELLER', 'NORMAL', '2026-01-18 18:33:48', '2026-01-18 09:33:59');

-- 상품 데이터 (category_id 15번이 존재한다고 가정)
INSERT INTO `products` (`product_id`, `seller_id`, `category_id`, `title`, `description`, `status`, `view_count`, `created_at`)
VALUES (1, 1, 15, 'LG 트롬 오브제컬렉션 워시콤보', '올인원 세탁·건조\n\n하나의 통 안에서 한 번에\n똑똑한 세탁·건조\n무거운 세탁물을 옮기는 번거로움 없이 세탁기 한 대로 공간을 절약하고\n인공지능 코스를 활용해 섬세한 세탁부터 똑똑한 건조까지 99분 만에 끝내보세요.\n\n올인원 컴팩트 디자인\n\n공간에 감각을 더하는 디자인\n어느 공간에나 조화롭게 녹아들어 취향에 따라 다양하게 선택할 수 있는 다섯가지 색상과\n심플하고 미니멀한 플랫 디자인이 당신의 공간에 모던하고 고급스러운 분위기를 더해줍니다.\n\n인공지능 DD모터 x 6모션\n\n인공지능 DD모터로\n섬세한 세탁·건조\n섬세하고 정확한 움직임이 가능한 DD모터를 통해\n손빨래 동작, 자연 건조를 닮은 6가지 모션을 구현합니다.\n\n인버터 히트펌프 건조\n\n옷감에서 느껴지는\n건조 기술\n인버터 히트펌프 방식으로 저온 건조해 옷감을 보호하고\n균일한 건조로 섬세한 차이를 느껴보세요.\n\n5방향 터보샷+\n\n강력한 물살로\n깨끗하게\n다섯 방향의 터보샷+로 세제와 유연제를 고루고루\n시간과 물을 절약하며 깨끗하게 세탁합니다.\n\n인공지능 세탁·건조 코스\n\n세탁물의 특성에 맞춰\nAI가 알아서 세탁부터 건조까지\n의류 특성과 오염도에 맞춘 최적화된 방법으로 세탁, 탈수, 건조까지 99분이면 충분합니다.\nAI 기술로 더 섬세하게 세탁·건조 하세요.\n\n소량 급속 코스\n\n소량의 세탁물은\n소량 급속 코스로 더욱 빠르게\n갑자기 일정이 생겼을 때, 시간이 부족하다면\n바쁜 일상 속 적은 양의 세탁물은 소량급속 코스를 활용해보세요.\n약 1시간 내에 세탁부터 건조까지 완료 가능합니다.\n\n미세플라스틱 케어 코스\n\n환경까지 생각한\n미세플라스틱 저감 세탁\n6모션 중 옷감 마찰을 줄이는 흔들기, 비비기, 주무르기 모션과 강력한 5방향 터보샷이\n옷감 손상과 미세 플라스틱 배출을 70% 줄여주고, 에너지 사용량도 30% 줄여줍니다.', 'ACTIVE', 12, '2026-01-18 18:35:50');

-- 상품 이미지 데이터
INSERT INTO `product_images` (`image_id`, `product_id`, `image_url`, `image_order`) VALUES
                                                                                        (1, 1, 'https://picsum.photos/400/300?random=1', 1),
                                                                                        (2, 1, 'https://picsum.photos/400/300?random=2', 2),
                                                                                        (3, 1, 'https://picsum.photos/400/300?random=3', 3),
                                                                                        (4, 1, 'https://picsum.photos/400/300?random=4', 4),
                                                                                        (5, 1, 'https://picsum.photos/400/300?random=5', 5);

-- 경매 데이터
INSERT INTO `auctions` (`auction_id`, `product_id`, `start_price`, `current_price`, `start_time`, `end_time`, `status`)
VALUES (1, 1, 3800000, 3800000, '2026-01-18 18:36:00', '2026-04-25 18:34:00', 'PROCEEDING');

-- 4. 외래 키 체크 재활성화
SET FOREIGN_KEY_CHECKS = 1;