LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'생활용품',NULL,'1')
                              ,(2,'가전제품',NULL,'2')
                              ,(3,'식품',NULL,'3')
                              ,(4,'PC/노트북',2,'2/4')
                              ,(5,'LG 노트북',4,'2/4/5')
                              ,(6,'삼성 노트북',4,'2/4/6')
                              ,(7,'HP 노트북',4,'2/4/7')
                              ,(8,'ASUS 노트북',4,'2/4/8')
                              ,(9,'삼성 PC',4,'2/4/9')
                              ,(10,'LG PC',4,'2/4/10')
                              ,(11,'세탁기',2,'2/11')
                              ,(12,'냉장고',12,'2/12')
                              ,(13,'에어컨',13,'2/13')
                              ,(14,'통돌이 세탁기',11,'2/11/14')
                              ,(15,'LG 세탁기',11,'2/11/15')
                              ,(16,'삼성 세탁기',11,'2/11/16')

/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;