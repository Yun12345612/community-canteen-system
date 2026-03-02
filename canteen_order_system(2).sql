/*
 Navicat Premium Dump SQL

 Source Server         : localhost8.0
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : canteen_order_system

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 02/03/2026 19:31:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_dish
-- ----------------------------
DROP TABLE IF EXISTS `t_dish`;
CREATE TABLE `t_dish`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜品唯一ID（主键）',
  `merchant_id` bigint(20) NOT NULL COMMENT '所属商家ID（关联t_merchant表id）',
  `dish_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称',
  `category_id` bigint(20) NOT NULL COMMENT '关联菜品分类ID',
  `price` decimal(10, 2) NOT NULL COMMENT '菜品单价',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '菜品库存数量',
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜品封面图片URL',
  `taste_tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '口味标签（多个用逗号分隔，如微辣,清淡）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜品描述',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '菜品状态（1-上架，0-下架）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE COMMENT '按分类查询菜品索引'
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜品表（商家端核心管理）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_dish
-- ----------------------------
INSERT INTO `t_dish` VALUES (1, 7, '麻辣小龙虾', 1, 78.80, 76, 'http://localhost:8080/uploads/ebee374be0ab4592829ca2112b0dabac_1769781803323_26.JPG', '麻辣、鲜香', '麻辣入味', 1, '2026-01-28 11:18:55', '2026-03-02 19:14:30', NULL);
INSERT INTO `t_dish` VALUES (2, 7, '西瓜汁', 5, 50.01, 39943, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', '', '清热解暑', 1, '2026-01-29 13:26:35', '2026-03-02 19:14:29', NULL);
INSERT INTO `t_dish` VALUES (3, 7, '生椰拿铁', 6, 20.00, 80000, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', NULL, '清凉一夏', 1, '2026-01-29 14:07:06', '2026-03-01 16:53:16', NULL);
INSERT INTO `t_dish` VALUES (4, 7, '肉夹馍', 5, 0.04, 7992, 'http://localhost:8080/uploads/4fc23ec8ddef4d24835ed4543eeadf0c_1769781794011_276.JPG', NULL, '西安特色', 1, '2026-01-29 16:30:38', '2026-02-26 18:08:40', NULL);
INSERT INTO `t_dish` VALUES (5, 7, '佛跳墙', 4, 10000.01, 790, 'http://127.0.0.1:8080/uploads/60d2682c33944c84b67937efab76eaae_1772354440526_571.png', NULL, '福建特色', 1, '2026-02-05 19:48:38', '2026-03-01 16:40:48', NULL);
INSERT INTO `t_dish` VALUES (6, 7, '抹茶', 1, 0.06, 7985, 'http://127.0.0.1:8080/uploads/322682a43f194aa1838c7665bd247051_1772354367600_914.png', NULL, '好喝', 1, '2026-02-06 12:43:17', '2026-03-01 16:39:36', NULL);
INSERT INTO `t_dish` VALUES (7, 7, '哈哈', 1, 0.03, 7994, 'http://127.0.0.1:8080/uploads/f250dda459fa4e1e9a6b63eda1ddd682_1772353554643_690.jpg', NULL, '测试回显', 1, '2026-02-23 16:03:28', '2026-03-02 18:19:14', NULL);
INSERT INTO `t_dish` VALUES (8, 8, '1', 0, 0.02, 2, 'http://127.0.0.1:8080/uploads/861555e04447487caa2b2d9610fa9933_1772352099442_232.jpg', NULL, '111', 1, '2026-03-01 16:01:40', '2026-03-01 16:01:45', NULL);
INSERT INTO `t_dish` VALUES (9, 7, '1212', 2, 0.02, 2, 'http://127.0.0.1:8080/uploads/2be3e3edf3c5471da970f9f788786f2b_1772354394217_597.jpg', NULL, '', 1, '2026-03-01 16:39:55', '2026-03-01 16:42:08', NULL);

-- ----------------------------
-- Table structure for t_dish_category
-- ----------------------------
DROP TABLE IF EXISTS `t_dish_category`;
CREATE TABLE `t_dish_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类唯一ID（主键）',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称（如热菜、凉菜、主食）',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序权重（数值越小越靠前）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_valid` tinyint(1) NULL DEFAULT 1 COMMENT '分类状态（1-启用，0-禁用）',
  `merchant_id` bigint(20) NULL DEFAULT NULL COMMENT '关联商家ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_dish_category
-- ----------------------------
INSERT INTO `t_dish_category` VALUES (1, '热菜', 1, '2026-01-29 13:24:29', '2026-01-29 13:24:29', 1, 7);
INSERT INTO `t_dish_category` VALUES (2, '凉菜', 2, '2026-01-29 13:24:29', '2026-01-29 13:24:29', 1, 7);
INSERT INTO `t_dish_category` VALUES (3, '汤品', 3, '2026-01-29 13:24:29', '2026-01-29 13:24:29', 1, 7);
INSERT INTO `t_dish_category` VALUES (4, '主食', 4, '2026-01-29 13:24:29', '2026-01-29 13:24:29', 1, 7);
INSERT INTO `t_dish_category` VALUES (5, '小吃', 5, '2026-01-29 13:24:29', '2026-01-29 13:24:29', 1, 7);
INSERT INTO `t_dish_category` VALUES (6, '饮品', 6, '2026-01-29 13:24:29', '2026-01-29 13:24:29', 1, 7);

-- ----------------------------
-- Table structure for t_dish_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `t_dish_evaluation`;
CREATE TABLE `t_dish_evaluation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜品评价ID',
  `evaluation_id` bigint(20) NULL DEFAULT NULL COMMENT '订单评价ID',
  `order_item_id` bigint(20) NULL DEFAULT NULL COMMENT '订单项ID',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品ID',
  `dish_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `score` tinyint(4) NOT NULL COMMENT '评分（1-5星）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_evaluation_id`(`evaluation_id` ASC) USING BTREE,
  INDEX `idx_dish_id`(`dish_id` ASC) USING BTREE,
  INDEX `idx_order_item_id`(`order_item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜品评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_dish_evaluation
-- ----------------------------
INSERT INTO `t_dish_evaluation` VALUES (1, 1, 10, 1, '麻辣小龙虾', NULL, 5, '小龙虾很新鲜，麻辣味适中，很好吃！', '2026-02-02 10:40:00', '2026-02-01 18:25:08');
INSERT INTO `t_dish_evaluation` VALUES (2, 1, 11, 3, '生椰拿铁', NULL, 4, '咖啡味道不错，椰香浓郁', '2026-02-02 10:40:00', '2026-02-01 18:25:08');
INSERT INTO `t_dish_evaluation` VALUES (3, 1, 12, 4, '肉夹馍', NULL, 5, '肉夹馍很正宗，肉馅多汁', '2026-02-02 10:40:00', '2026-02-01 18:25:08');
INSERT INTO `t_dish_evaluation` VALUES (4, 14, 1001, 1, '测试菜品', NULL, 5, '手动插入的菜品评价', '2026-02-23 17:50:28', '2026-02-23 17:50:28');
INSERT INTO `t_dish_evaluation` VALUES (5, 14, 1001, 1, '测试菜品', NULL, 5, '手动插入的菜品评价', '2026-02-23 17:50:32', '2026-02-23 17:50:32');
INSERT INTO `t_dish_evaluation` VALUES (6, 14, 1001, 1, '测试菜品', NULL, 5, '手动插入的菜品评价', '2026-02-23 17:50:33', '2026-02-23 17:50:33');
INSERT INTO `t_dish_evaluation` VALUES (7, 21, 2020486959135199200, 2, '西瓜汁', NULL, 5, '11111', '2026-02-25 22:40:55', '2026-02-25 22:40:55');
INSERT INTO `t_dish_evaluation` VALUES (8, 21, 2020486959135199200, 3, '生椰拿铁', NULL, 5, '22222', '2026-02-25 22:40:55', '2026-02-25 22:40:55');
INSERT INTO `t_dish_evaluation` VALUES (9, 22, 2025837358973198300, 2, '西瓜汁', NULL, 5, '1111111', '2026-02-25 22:41:55', '2026-02-25 22:41:55');
INSERT INTO `t_dish_evaluation` VALUES (10, 23, 2026669723840749600, 3, '生椰拿铁', NULL, 5, '还吃1人 隔热管', '2026-02-25 22:45:48', '2026-02-25 22:45:48');
INSERT INTO `t_dish_evaluation` VALUES (11, 24, 2026671102265204700, 2, '西瓜汁', NULL, 5, '777', '2026-02-25 22:50:54', '2026-02-25 22:50:54');
INSERT INTO `t_dish_evaluation` VALUES (12, 24, 2026671102265204700, 3, '生椰拿铁', NULL, 5, '888', '2026-02-25 22:50:54', '2026-02-25 22:50:54');
INSERT INTO `t_dish_evaluation` VALUES (13, 25, 2026678863422103600, 3, '生椰拿铁', NULL, 5, '对不对方便', '2026-02-25 23:33:33', '2026-02-25 23:33:33');
INSERT INTO `t_dish_evaluation` VALUES (14, 26, 2026934459022966800, 2, '西瓜汁', NULL, 5, '和人多好多好', '2026-02-26 16:17:30', '2026-02-26 16:17:30');
INSERT INTO `t_dish_evaluation` VALUES (15, 26, 2026934459022966800, 3, '生椰拿铁', NULL, 5, '当当个', '2026-02-26 16:17:30', '2026-02-26 16:17:30');
INSERT INTO `t_dish_evaluation` VALUES (16, 27, 2026954174839652400, 2, '西瓜汁', NULL, 5, '儿童画', '2026-02-26 17:35:46', '2026-02-26 17:35:46');
INSERT INTO `t_dish_evaluation` VALUES (17, 28, 2026954609315020800, 2, '西瓜汁', NULL, 5, '很符合', '2026-02-26 17:37:38', '2026-02-26 17:37:38');
INSERT INTO `t_dish_evaluation` VALUES (18, 29, 2026955455905923000, 1, '麻辣小龙虾', NULL, 5, '热水', '2026-02-26 17:40:43', '2026-02-26 17:40:43');
INSERT INTO `t_dish_evaluation` VALUES (19, 29, 2026955455973032000, 2, '西瓜汁', NULL, 5, '东方红', '2026-02-26 17:40:43', '2026-02-26 17:40:43');
INSERT INTO `t_dish_evaluation` VALUES (20, 29, 2026955455973032000, 4, '肉夹馍', NULL, 5, '动画', '2026-02-26 17:40:43', '2026-02-26 17:40:43');
INSERT INTO `t_dish_evaluation` VALUES (21, 29, 2026955455973032000, 5, '佛跳墙', NULL, 5, '惹的祸', '2026-02-26 17:40:43', '2026-02-26 17:40:43');
INSERT INTO `t_dish_evaluation` VALUES (22, 29, 2026955455973032000, 6, '抹茶', NULL, 5, '挺好', '2026-02-26 17:40:43', '2026-02-26 17:40:43');
INSERT INTO `t_dish_evaluation` VALUES (23, 29, 2026955455973032000, 7, '哈哈', NULL, 5, '人儿', '2026-02-26 17:40:43', '2026-02-26 17:40:43');
INSERT INTO `t_dish_evaluation` VALUES (24, 30, 2026960316517064700, 6, '抹茶', NULL, 5, '柔荑花', '2026-02-26 17:59:59', '2026-02-26 17:59:59');
INSERT INTO `t_dish_evaluation` VALUES (25, 31, 2026960008143446000, 6, '抹茶', NULL, 5, ' 然后', '2026-02-26 18:00:13', '2026-02-26 18:00:13');
INSERT INTO `t_dish_evaluation` VALUES (26, NULL, NULL, 7, '哈哈', 8, 5, '好好好！！！', '2026-03-02 00:58:28', '2026-03-02 00:58:28');
INSERT INTO `t_dish_evaluation` VALUES (27, 33, 2028414857892057090, 2, '西瓜汁', 5, 5, 'as', '2026-03-02 18:47:36', '2026-03-02 18:47:36');
INSERT INTO `t_dish_evaluation` VALUES (28, 33, 2028414857892057091, 7, '哈哈', 5, 5, 'as', '2026-03-02 18:47:36', '2026-03-02 18:47:36');
INSERT INTO `t_dish_evaluation` VALUES (29, 33, 2028414857892057092, 3, '生椰拿铁', 5, 5, 'as', '2026-03-02 18:47:36', '2026-03-02 18:47:36');
INSERT INTO `t_dish_evaluation` VALUES (30, 32, 2028028016122654721, 9, '1212', 5, 5, 'asas', '2026-03-02 18:47:56', '2026-03-02 18:47:56');
INSERT INTO `t_dish_evaluation` VALUES (31, 36, 2028428775473418241, 1, '麻辣小龙虾', 5, 5, 'qqqqqqqqqqqqqqqq', '2026-03-02 19:15:21', '2026-03-02 19:15:21');
INSERT INTO `t_dish_evaluation` VALUES (32, 36, 2028428775473418242, 2, '西瓜汁', 5, 5, 'qqqqqqqqqqqq', '2026-03-02 19:15:21', '2026-03-02 19:15:21');

-- ----------------------------
-- Table structure for t_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `t_evaluation`;
CREATE TABLE `t_evaluation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID（主键）',
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `merchant_id` bigint(20) NOT NULL COMMENT '所属商家ID（商家评价数据隔离）',
  `score` tinyint(1) NOT NULL COMMENT '评分（1-5星）',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '评价内容',
  `img_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '评价图片URL（多个用逗号分隔）',
  `merchant_reply` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '商家回复内容',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '商家回复时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE COMMENT '一个订单仅一条评价',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '关联用户索引',
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE COMMENT '关联订单索引（商家端查评价）',
  INDEX `idx_merchant_id`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户评价表（商家端可回复）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_evaluation
-- ----------------------------
INSERT INTO `t_evaluation` VALUES (25, 'dc14764f5cdb41be89994d9c74cbdbd7', 2, 7, 5, '烧过水给我', 'http://127.0.0.1:8080/uploads/6478bf55cb8b44faa540bd9e4b080515_1772033609776_572.jpg', 'drghbdhgeh', '2026-02-25 23:33:55', '2026-02-25 23:33:33', '2026-02-25 23:33:55');
INSERT INTO `t_evaluation` VALUES (26, '76450a3ad58248e793f8dec8fdc76fb0', 2, 7, 5, '', 'http://127.0.0.1:8080/uploads/546b4b017ac74cedb4236aeaba41847c_1772093847623_848.jpg', '是的归属感', '2026-02-26 16:18:18', '2026-02-26 16:17:30', '2026-02-26 16:18:18');
INSERT INTO `t_evaluation` VALUES (27, 'b5f389e2ce5a47ec86e1dd179cb5debb', 4, 7, 5, '好地方', 'http://127.0.0.1:8080/uploads/5e500488159745c7845f8dd647212b95_1772098544465_180.jpg', '', NULL, '2026-02-26 17:35:45', '2026-02-26 17:35:45');
INSERT INTO `t_evaluation` VALUES (28, '476981b7ce6e4678ae044f116fe67c5b', 4, 7, 5, '多喝点', 'http://127.0.0.1:8080/uploads/3608bbe03e9845edae05a1448abe841c_1772098656293_63.jpg', '', NULL, '2026-02-26 17:37:37', '2026-02-26 17:37:37');
INSERT INTO `t_evaluation` VALUES (29, '7b5772c334a84b9ca8681a91e4327ce4', 4, 7, 5, ' 好', 'http://127.0.0.1:8080/uploads/886ed5d511ea4d3db6d9d8476da4b956_1772098841879_392.jpg', '', NULL, '2026-02-26 17:40:42', '2026-02-26 17:40:42');
INSERT INTO `t_evaluation` VALUES (30, 'bd59b39968e54ff6abfe19af2ec11eeb', 4, 7, 5, '让他111', 'http://127.0.0.1:8080/uploads/583cfec2d52b4a0aaead1a6413847393_1772099998125_72.jpg', '', NULL, '2026-02-26 17:59:59', '2026-03-01 16:40:57');
INSERT INTO `t_evaluation` VALUES (31, '9c77f297a4104c3aa29b369ec838dee3', 4, 7, 5, ' 热好1', 'http://127.0.0.1:8080/uploads/a246e7baa9c6490a99dc7d558b390dbe_1772100011752_671.jpg', 'asas', '2026-03-01 16:26:42', '2026-02-26 18:00:12', '2026-03-01 16:38:41');
INSERT INTO `t_evaluation` VALUES (32, '2ede3fa03f11454ca2f9f2cfdc787704', 5, 7, 5, '2', 'http://127.0.0.1:8080/uploads/4f6cc7a0d8b742c696dbb9ab13e4183a_1772357113737_397.png', 'qwq', '2026-03-01 17:28:36', '2026-03-01 17:25:16', '2026-03-01 17:28:36');
INSERT INTO `t_evaluation` VALUES (33, '79175af1e6194c25947dbbd82c85441d', 5, 7, 5, NULL, NULL, '', NULL, '2026-03-02 18:20:08', '2026-03-02 18:20:08');
INSERT INTO `t_evaluation` VALUES (34, 'feed9b2e2d114c0e8f5f6f172c98709d', 5, 7, 5, '啊打我的', 'http://127.0.0.1:8080/uploads/0f671600e848434dbfe4fa122b85da08_1772446894414_299.png', 'saCSDC', '2026-03-02 18:24:58', '2026-03-02 18:23:19', '2026-03-02 18:24:58');
INSERT INTO `t_evaluation` VALUES (35, '94a36bb1a33c4178bff952e37deb7de8', 5, 7, 5, 'as', NULL, '', NULL, '2026-03-02 18:48:08', '2026-03-02 18:48:08');
INSERT INTO `t_evaluation` VALUES (36, 'e66495e438724b2d8a377b4970787fab', 5, 7, 5, 'qqqqqqqqqqqqqqqq', 'http://127.0.0.1:8080/uploads/8ff7bb45e056421ab06b171ec701a436_1772450104899_973.png', '', NULL, '2026-03-02 19:15:05', '2026-03-02 19:15:05');

-- ----------------------------
-- Table structure for t_merchant
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant`;
CREATE TABLE `t_merchant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商家唯一ID（主键）',
  `merchant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食堂名称',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家登录账号（唯一，字母/数字组合）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '商家展示用户名（可重复，非登录标识）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码（建议加密存储）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家登录手机号（唯一，11位纯数字）',
  `business_hours` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '营业时间段（如：早餐6:30-9:30）',
  `is_holiday` tinyint(1) NULL DEFAULT 0 COMMENT '是否休假（0-正常营业，1-休假）',
  `holiday_start` date NULL DEFAULT NULL COMMENT '休假开始日期',
  `holiday_end` date NULL DEFAULT NULL COMMENT '休假结束日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '信息更新时间',
  `is_valid` tinyint(1) NULL DEFAULT 1 COMMENT '账号状态（1-正常，0-禁用）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account`(`account` ASC) USING BTREE COMMENT '登录账号唯一约束',
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE COMMENT '商家手机号唯一约束',
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE COMMENT '登录账号唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商家/管理员表（商家端核心）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_merchant
-- ----------------------------
INSERT INTO `t_merchant` VALUES (7, '幸福里社区食堂', '5201314', '幸福里社区食堂', '$2a$10$z.soKXufRM28jvp/us.ZaeIithNctiC2rloz/v3DZlMX7dkJ/SQyy', '15659394046', NULL, 1, '2026-02-26', '2026-02-27', '2026-01-28 19:45:44', '2026-03-01 16:25:29', 1);
INSERT INTO `t_merchant` VALUES (8, '测试商家', '111111', '测试商家', '$2a$10$z.soKXufRM28jvp/us.ZaeIithNctiC2rloz/v3DZlMX7dkJ/SQyy', '18889898989', NULL, 0, NULL, NULL, '2026-03-01 15:15:40', '2026-03-01 15:15:40', 1);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单ID（UUID）',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号（自定义生成，如YYYYMMDD+随机数）',
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `merchant_id` bigint(20) NOT NULL COMMENT '关联商家ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `dining_type` tinyint(1) NOT NULL COMMENT '就餐模式（1-堂食，2-外带）',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '订单备注（如少盐、免辣）',
  `pay_status` tinyint(1) NULL DEFAULT 0 COMMENT '支付状态（0-未支付，1-已支付，2-退款中，3-已退款）',
  `order_status` tinyint(1) NULL DEFAULT 1 COMMENT '订单状态：1-待接单，2-待取餐，3-已完成，4-已取消',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `expected_time` datetime NULL DEFAULT NULL COMMENT '预计完成时间',
  `actual_complete_time` datetime NULL DEFAULT NULL COMMENT '实际完成时间',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '取消原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `receiver_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人地址	',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE COMMENT '订单编号唯一',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '查询用户订单索引',
  INDEX `idx_order_status`(`order_status` ASC) USING BTREE COMMENT '按订单状态筛选（商家端核心）',
  INDEX `idx_pay_status`(`pay_status` ASC) USING BTREE COMMENT '按支付状态筛选',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '按时间查询订单（如今日订单）',
  INDEX `idx_merchant_id`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES ('02c064c9b3ee4cdfbcaf7e3e65e15b37', '202602261803431114755', 4, 7, 10000.04, 1, '个是', 2, 3, '2026-02-26 18:03:46', NULL, '2026-02-26 18:03:52', '', '2026-02-26 18:03:43', '2026-02-26 18:03:52', '个', '15060602020', '给我个');
INSERT INTO `t_order` VALUES ('0d2a42ee47274e6fa25d5d66a1d68198', '20260226180229181463', 4, 7, 10000.00, 1, '热用户', 2, 3, '2026-02-26 18:02:32', NULL, '2026-02-26 18:02:38', '', '2026-02-26 18:02:29', '2026-02-26 18:02:38', '让个人', '15060604040', '反而更');
INSERT INTO `t_order` VALUES ('0d6c6d0d116d4231bd44a9601dc8357c', '202602081355584121459', 1, 7, 20.00, 1, '哈哈', 2, 3, '2026-02-08 13:56:01', NULL, '2026-02-23 16:25:43', '', '2026-02-08 13:55:58', '2026-02-23 16:25:43', '原因', '15060660666', '哈哈');
INSERT INTO `t_order` VALUES ('17bcc579492f4f3a9d39ff739500ba6d', '202602231753550992521', 2, 7, 50.01, 1, '舒服舒服', 2, 3, '2026-02-23 17:53:58', NULL, '2026-02-23 17:54:14', '', '2026-02-23 17:53:55', '2026-02-23 17:54:14', '提供关于', '15659292055', '发顺丰');
INSERT INTO `t_order` VALUES ('1da4c222f0e64d1e912d2b89aa141c91', '202602252250191732832', 2, 7, 140.02, 1, '89498', 2, 3, '2026-02-25 22:50:22', NULL, '2026-02-25 22:50:34', '', '2026-02-25 22:50:19', '2026-02-25 22:50:34', '7897989', '15958587044', '548954');
INSERT INTO `t_order` VALUES ('2ede3fa03f11454ca2f9f2cfdc787704', '202603011642126365704', 5, 7, 0.02, 1, '', 2, 3, '2026-03-01 16:42:16', NULL, '2026-03-01 17:21:35', '', '2026-03-01 16:42:13', '2026-03-01 17:21:35', '应急广播-C01', '13880010005', '玉泉路');
INSERT INTO `t_order` VALUES ('3217f9ed036b46708d1d052f2962d847', '202602081745342132102', 2, 7, 20.00, 1, '分隔符', 2, 3, '2026-02-08 17:45:37', NULL, '2026-02-08 17:45:57', '', '2026-02-08 17:45:34', '2026-02-08 17:45:57', '哥哥', '16050501010', '哥哥');
INSERT INTO `t_order` VALUES ('376f2f4135ca46b9984cfbed6ee50da3', '20260223151709987252', 2, 7, 20.00, 1, '发给个', 2, 3, '2026-02-23 15:17:14', NULL, '2026-02-23 15:17:37', '', '2026-02-23 15:17:10', '2026-02-23 15:17:37', '滚滚滚', '15659394044', '哥哥');
INSERT INTO `t_order` VALUES ('444c476ad31c42adb5b2149c25b58b73', '202602082121046532827', 2, 7, 500.10, 1, '订单', 2, 3, '2026-02-08 21:21:09', NULL, '2026-02-08 21:21:46', '', '2026-02-08 21:21:05', '2026-02-08 21:21:46', '哥哥', '13650505055', '方法');
INSERT INTO `t_order` VALUES ('476981b7ce6e4678ae044f116fe67c5b', '202602261736525164814', 4, 7, 1600.32, 1, '的士速递', 2, 3, '2026-02-26 17:36:56', NULL, '2026-02-26 17:37:19', '', '2026-02-26 17:36:53', '2026-02-26 17:37:19', '日光灯管', '15020201011', '更好地给');
INSERT INTO `t_order` VALUES ('530aca259a984c8b867b3d0fc66dccc5', '202602081858111982585', 2, 7, 20.00, 1, '浏览', 2, 3, '2026-02-08 18:58:16', NULL, '2026-02-08 18:58:42', '', '2026-02-08 18:58:11', '2026-02-08 18:58:42', '哥哥', '15050505055', '浏览');
INSERT INTO `t_order` VALUES ('62cf9d54eb54485b9c1260a6b5359bde', '202602261803126744211', 4, 7, 10000.04, 1, '个热', 2, 3, '2026-02-26 18:03:15', NULL, '2026-02-26 18:03:22', '', '2026-02-26 18:03:13', '2026-02-26 18:03:22', '我问他吧', '15060602020', '法国人');
INSERT INTO `t_order` VALUES ('63d037e54c544dd2bd0684b313c2ad82', '202602261809035182773', 2, 7, 50070.13, 1, '个凡尔赛', 2, 3, '2026-02-26 18:09:06', NULL, '2026-02-26 18:09:13', '', '2026-02-26 18:09:04', '2026-02-26 18:09:13', '3 有15060605050', '15060601010', '温氏股份');
INSERT INTO `t_order` VALUES ('63ea080fd3d24b8ba26ae03419520f31', '20260208151826499264', 2, 7, 20.00, 1, '哈哈', 2, 3, '2026-02-08 15:18:29', NULL, '2026-02-08 15:18:48', '', '2026-02-08 15:18:26', '2026-02-08 15:18:48', '11', '15050505055', '哈哈');
INSERT INTO `t_order` VALUES ('73776e2808d049b5ba57d16029364741', '202602231534341472763', 2, 7, 50.01, 1, '隔热管', 2, 3, '2026-02-23 15:34:37', NULL, '2026-02-23 15:35:03', '', '2026-02-23 15:34:34', '2026-02-23 15:35:03', '烧过水的过程', '13669694055', 'UI哦会开户');
INSERT INTO `t_order` VALUES ('76450a3ad58248e793f8dec8fdc76fb0', '202602261616483122757', 2, 7, 140.02, 1, '微服务', 2, 3, '2026-02-26 16:16:51', NULL, '2026-02-26 16:17:12', '', '2026-02-26 16:16:48', '2026-02-26 16:17:12', '谁知道VS大V', '15050505050', '前阿飞');
INSERT INTO `t_order` VALUES ('76f7da85f20a4da6a1c972db06b4f105', '202602081505024241165', 1, 7, 20.00, 1, '浏览', 2, 3, '2026-02-08 15:05:06', NULL, '2026-02-08 15:05:28', '', '2026-02-08 15:05:02', '2026-02-08 15:05:28', '版本', '18090995066', '哈哈');
INSERT INTO `t_order` VALUES ('79175af1e6194c25947dbbd82c85441d', '202603021819228975873', 5, 7, 70.04, 2, '钱王拳王', 2, 3, '2026-03-02 18:19:26', NULL, '2026-03-02 18:19:32', '', '2026-03-02 18:19:23', '2026-03-02 18:19:32', '应急广播-C01', '13880010005', '玉泉路');
INSERT INTO `t_order` VALUES ('7b01376c8b1248b281054d0abdd92d8d', '202602261615177742293', 2, 7, 110.01, 1, '放松放松', 2, 3, '2026-02-26 16:15:21', NULL, '2026-03-01 18:19:51', '', '2026-02-26 16:15:18', '2026-03-01 18:19:51', '规范VS个风扇', '15850504055', '舒服舒服');
INSERT INTO `t_order` VALUES ('7b5772c334a84b9ca8681a91e4327ce4', '202602261740143594754', 4, 7, 30386.73, 1, '反而更', 2, 3, '2026-02-26 17:40:18', NULL, '2026-02-26 17:40:23', '', '2026-02-26 17:40:14', '2026-02-26 17:40:23', '等会让', '15060602020', '个人');
INSERT INTO `t_order` VALUES ('7b5c8099620e44f7951d034346f3fab7', '202602142032226462644', 2, 7, 190.03, 1, '哥哥', 2, 3, '2026-02-14 20:32:27', NULL, '2026-02-14 20:33:35', '', '2026-02-14 20:32:23', '2026-02-14 20:33:35', '哈哈哈', '15959594055', '哈哈');
INSERT INTO `t_order` VALUES ('8138796e6ed34641a431231d6fd91459', '202602231537192622765', 2, 7, 50.01, 1, '发顺丰', 2, 3, '2026-02-23 15:37:23', NULL, '2026-02-23 16:25:41', '', '2026-02-23 15:37:19', '2026-02-23 16:25:41', '滚滚滚', '15020201011', '当当是');
INSERT INTO `t_order` VALUES ('9420fe4c0fe641389bd0de0e835f2a47', '202602081445378801622', 1, 7, 20.00, 1, '订单', 2, 3, '2026-02-08 14:45:41', NULL, '2026-02-08 14:46:12', '', '2026-02-08 14:45:38', '2026-02-08 14:46:12', '哥哥', '15060665066', '浏览');
INSERT INTO `t_order` VALUES ('94a36bb1a33c4178bff952e37deb7de8', '202603011626169965465', 5, 7, 10000.00, 1, '', 2, 3, '2026-03-01 16:26:20', NULL, '2026-03-01 18:19:50', '', '2026-03-01 16:26:17', '2026-03-01 18:19:50', '应急广播-C01', '13880010005', '玉泉路');
INSERT INTO `t_order` VALUES ('9c77f297a4104c3aa29b369ec838dee3', '202602261758196764845', 4, 7, 0.48, 1, '让它发挥', 2, 3, '2026-02-26 17:58:23', NULL, '2026-02-26 17:58:30', '', '2026-02-26 17:58:20', '2026-02-26 17:58:30', '还让他', '15060601010', '柔荑花');
INSERT INTO `t_order` VALUES ('a217b7cd3851459d9a9d2f3430b15638', '202602082132044682826', 2, 7, 20.00, 1, '问问', 2, 3, '2026-02-08 21:32:07', NULL, '2026-02-08 21:40:38', '', '2026-02-08 21:32:04', '2026-02-08 21:40:38', '问问', '15050505050', '问问');
INSERT INTO `t_order` VALUES ('abfce651c4b540798ed13879630a9f62', '202602081803200512410', 2, 7, 20.00, 1, '方法', 2, 3, '2026-02-08 18:03:23', NULL, '2026-02-08 18:04:01', '', '2026-02-08 18:03:20', '2026-02-08 18:04:01', '问问', '15060607044', '哥哥');
INSERT INTO `t_order` VALUES ('b190c0393cec4d5cb7500e8ad12773ec', '202602081455599331861', 1, 7, 20.00, 1, '好', 2, 3, '2026-02-08 14:56:03', NULL, '2026-02-08 14:56:22', '', '2026-02-08 14:56:00', '2026-02-08 14:56:22', '哈哈', '16060660600', '哈哈');
INSERT INTO `t_order` VALUES ('b5f389e2ce5a47ec86e1dd179cb5debb', '20260226173508931443', 4, 7, 1050.21, 1, '无法哥哥', 2, 3, '2026-02-26 17:35:12', NULL, '2026-02-26 17:35:22', '', '2026-02-26 17:35:09', '2026-02-26 17:35:22', '和人太好', '15020201011', '微服务');
INSERT INTO `t_order` VALUES ('bd59b39968e54ff6abfe19af2ec11eeb', '202602261759332054432', 4, 7, 0.04, 1, '然后', 2, 3, '2026-02-26 17:59:36', NULL, '2026-02-26 17:59:45', '', '2026-02-26 17:59:33', '2026-02-26 17:59:45', '而已', '15060601010', '给人的人工');
INSERT INTO `t_order` VALUES ('c00f00b9454c4a78b9e5204dc87d4cd8', '202602080102240461358', 1, 7, 20.00, 1, '哈哈', 2, 3, '2026-02-08 01:02:29', NULL, '2026-02-08 01:04:21', '', '2026-02-08 01:02:24', '2026-02-08 01:04:21', '哈哈', '15650505055', '哈哈');
INSERT INTO `t_order` VALUES ('c2a36ad46deb4358a163afb7da338c56', '202602080102511741801', 1, 7, 20.00, 1, '浏览', 2, 3, '2026-02-08 01:03:06', NULL, '2026-02-23 16:25:45', '', '2026-02-08 01:02:51', '2026-02-23 16:25:45', '那你', '16050508055', '浏览');
INSERT INTO `t_order` VALUES ('c37a6b4231f34f40bea379589a17f92d', '202602231544275132206', 2, 7, 50.01, 1, '沙发上', 2, 3, '2026-02-23 15:44:31', NULL, '2026-02-23 16:25:37', '', '2026-02-23 15:44:28', '2026-02-23 16:25:37', '问问', '15959594055', '大范甘迪');
INSERT INTO `t_order` VALUES ('cbf15945ff5d4606825b1d5915e04b19', '20260226180156069465', 4, 7, 10000.00, 1, '凡尔赛', 2, 3, '2026-02-26 18:01:59', NULL, '2026-02-26 18:02:10', '', '2026-02-26 18:01:56', '2026-02-26 18:02:10', '然后', '15020201010', '二等功');
INSERT INTO `t_order` VALUES ('d0f1e6b847514fd6adf1bb521077d9a5', '20260301162252965551', 5, 7, 0.04, 1, '', 2, 3, '2026-03-01 16:22:58', NULL, '2026-03-01 18:19:48', '', '2026-03-01 16:22:53', '2026-03-01 18:19:48', '应急广播-C01', '13880010005', '玉泉路');
INSERT INTO `t_order` VALUES ('d829482d954b4646880ed90c47bb7ed9', '202602252213386652908', 2, 7, 100.02, 1, '无法', 2, 3, '2026-02-25 22:13:41', NULL, '2026-02-25 22:13:57', '', '2026-02-25 22:13:39', '2026-02-25 22:13:57', '氛围', '15958584044', '鬼斧神工');
INSERT INTO `t_order` VALUES ('da065899c60349a48991ea558928e9a4', '202602082116445302391', 2, 7, 70.01, 2, '哥哥', 2, 3, '2026-02-08 21:16:48', NULL, '2026-02-08 21:21:53', '', '2026-02-08 21:16:45', '2026-02-08 21:21:53', '哈哈', '15659304056', '哥哥');
INSERT INTO `t_order` VALUES ('dc14764f5cdb41be89994d9c74cbdbd7', '202602252321095752339', 2, 7, 20.00, 1, '沙发上', 2, 3, '2026-02-25 23:21:12', NULL, '2026-02-25 23:21:20', '', '2026-02-25 23:21:10', '2026-02-25 23:21:20', '问问', '15969694055', '分公司分公司');
INSERT INTO `t_order` VALUES ('e66495e438724b2d8a377b4970787fab', '202603021914411145190', 5, 7, 128.81, 2, 'qqqq', 2, 3, '2026-03-02 19:14:44', NULL, '2026-03-02 19:14:50', '', '2026-03-02 19:14:41', '2026-03-02 19:14:50', '应急广播-C01', '13880010005', '玉泉路');
INSERT INTO `t_order` VALUES ('f6541e0be3864fba9f66eaa5e59d9885', '202602252244505192436', 2, 7, 20.00, 1, '49849', 2, 3, '2026-02-25 22:44:53', NULL, '2026-02-25 22:45:06', '', '2026-02-25 22:44:51', '2026-02-25 22:45:06', '7897', '15658587044', '489498');
INSERT INTO `t_order` VALUES ('f6e0451661844163ab13ff053f89a2db', '202602082139583962267', 2, 7, 20.00, 1, '浏览', 2, 3, '2026-02-08 21:40:02', NULL, '2026-02-23 15:16:02', '', '2026-02-08 21:39:58', '2026-02-23 15:16:02', '看看', '15060606055', '浏览');
INSERT INTO `t_order` VALUES ('feed9b2e2d114c0e8f5f6f172c98709d', '202603011641162175103', 5, 7, 0.03, 1, '杀貂蝉', 2, 3, '2026-03-01 16:41:19', NULL, '2026-03-01 18:19:47', '', '2026-03-01 16:41:16', '2026-03-01 18:19:47', '应急广播-C01', '13880010005', '玉泉路');

-- ----------------------------
-- Table structure for t_order_item
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单明细ID（主键）',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联订单ID',
  `dish_id` bigint(20) NOT NULL COMMENT '关联菜品ID',
  `dish_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称（冗余存储，避免菜品删除后无数据）',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '下单时菜品单价（冗余存储）',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计金额（单价×数量）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜品个性化备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE COMMENT '关联订单索引（商家端查订单明细）',
  INDEX `idx_dish_id`(`dish_id` ASC) USING BTREE COMMENT '关联菜品索引'
) ENGINE = InnoDB AUTO_INCREMENT = 2028428775473418243 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_order_item
-- ----------------------------
INSERT INTO `t_order_item` VALUES (2020181360069677058, 'c00f00b9454c4a78b9e5204dc87d4cd8', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 01:02:24', '');
INSERT INTO `t_order_item` VALUES (2020181473835978753, 'c2a36ad46deb4358a163afb7da338c56', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 01:02:51', '');
INSERT INTO `t_order_item` VALUES (2020376036076777473, '0d6c6d0d116d4231bd44a9601dc8357c', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 13:55:58', '');
INSERT INTO `t_order_item` VALUES (2020388532871307266, '9420fe4c0fe641389bd0de0e835f2a47', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 14:45:38', '');
INSERT INTO `t_order_item` VALUES (2020391141912977410, 'b190c0393cec4d5cb7500e8ad12773ec', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 14:56:00', '');
INSERT INTO `t_order_item` VALUES (2020393417239011330, '76f7da85f20a4da6a1c972db06b4f105', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 15:05:02', '');
INSERT INTO `t_order_item` VALUES (2020396789807554561, '63ea080fd3d24b8ba26ae03419520f31', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 15:18:26', '');
INSERT INTO `t_order_item` VALUES (2020433815923707905, '3217f9ed036b46708d1d052f2962d847', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 17:45:34', '');
INSERT INTO `t_order_item` VALUES (2020438286393163777, 'abfce651c4b540798ed13879630a9f62', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 18:03:20', '');
INSERT INTO `t_order_item` VALUES (2020452090468384769, '530aca259a984c8b867b3d0fc66dccc5', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 18:58:11', '');
INSERT INTO `t_order_item` VALUES (2020486959135199233, 'da065899c60349a48991ea558928e9a4', 2, '西瓜汁', 50.01, 1, 50.01, '2026-02-08 21:16:45', '');
INSERT INTO `t_order_item` VALUES (2020486959135199234, 'da065899c60349a48991ea558928e9a4', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 21:16:45', '');
INSERT INTO `t_order_item` VALUES (2020488050069475330, '444c476ad31c42adb5b2149c25b58b73', 2, '西瓜汁', 50.01, 10, 500.10, '2026-02-08 21:21:05', '');
INSERT INTO `t_order_item` VALUES (2020490817513197570, 'a217b7cd3851459d9a9d2f3430b15638', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 21:32:04', '');
INSERT INTO `t_order_item` VALUES (2020492805403578369, 'f6e0451661844163ab13ff053f89a2db', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-08 21:39:58', '');
INSERT INTO `t_order_item` VALUES (2022650121589239809, '7b5c8099620e44f7951d034346f3fab7', 3, '生椰拿铁', 20.00, 2, 40.00, '2026-02-14 20:32:23', '');
INSERT INTO `t_order_item` VALUES (2022650121610211330, '7b5c8099620e44f7951d034346f3fab7', 2, '西瓜汁', 50.01, 3, 150.03, '2026-02-14 20:32:23', '');
INSERT INTO `t_order_item` VALUES (2025832286897840130, '376f2f4135ca46b9984cfbed6ee50da3', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-23 15:17:10', '');
INSERT INTO `t_order_item` VALUES (2025836666405527554, '73776e2808d049b5ba57d16029364741', 2, '西瓜汁', 50.01, 1, 50.01, '2026-02-23 15:34:34', '');
INSERT INTO `t_order_item` VALUES (2025837358973198337, '8138796e6ed34641a431231d6fd91459', 2, '西瓜汁', 50.01, 1, 50.01, '2026-02-23 15:37:19', '');
INSERT INTO `t_order_item` VALUES (2025839155141943298, 'c37a6b4231f34f40bea379589a17f92d', 2, '西瓜汁', 50.01, 1, 50.01, '2026-02-23 15:44:28', '');
INSERT INTO `t_order_item` VALUES (2025871734809423874, '17bcc579492f4f3a9d39ff739500ba6d', 2, '西瓜汁', 50.01, 1, 50.01, '2026-02-23 17:53:55', '');
INSERT INTO `t_order_item` VALUES (2026661872737026049, 'd829482d954b4646880ed90c47bb7ed9', 2, '西瓜汁', 50.01, 2, 100.02, '2026-02-25 22:13:39', '');
INSERT INTO `t_order_item` VALUES (2026669723840749569, 'f6541e0be3864fba9f66eaa5e59d9885', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-25 22:44:51', '');
INSERT INTO `t_order_item` VALUES (2026671102265204737, '1da4c222f0e64d1e912d2b89aa141c91', 2, '西瓜汁', 50.01, 2, 100.02, '2026-02-25 22:50:19', '');
INSERT INTO `t_order_item` VALUES (2026671102265204738, '1da4c222f0e64d1e912d2b89aa141c91', 3, '生椰拿铁', 20.00, 2, 40.00, '2026-02-25 22:50:19', '');
INSERT INTO `t_order_item` VALUES (2026678863422103554, 'dc14764f5cdb41be89994d9c74cbdbd7', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-25 23:21:10', '');
INSERT INTO `t_order_item` VALUES (2026934079333597186, '7b01376c8b1248b281054d0abdd92d8d', 3, '生椰拿铁', 20.00, 3, 60.00, '2026-02-26 16:15:18', '');
INSERT INTO `t_order_item` VALUES (2026934079333597187, '7b01376c8b1248b281054d0abdd92d8d', 2, '西瓜汁', 50.01, 1, 50.01, '2026-02-26 16:15:18', '');
INSERT INTO `t_order_item` VALUES (2026934459022966786, '76450a3ad58248e793f8dec8fdc76fb0', 2, '西瓜汁', 50.01, 2, 100.02, '2026-02-26 16:16:48', '');
INSERT INTO `t_order_item` VALUES (2026934459022966787, '76450a3ad58248e793f8dec8fdc76fb0', 3, '生椰拿铁', 20.00, 2, 40.00, '2026-02-26 16:16:48', '');
INSERT INTO `t_order_item` VALUES (2026954174839652353, 'b5f389e2ce5a47ec86e1dd179cb5debb', 2, '西瓜汁', 50.01, 21, 1050.21, '2026-02-26 17:35:09', '');
INSERT INTO `t_order_item` VALUES (2026954609315020801, '476981b7ce6e4678ae044f116fe67c5b', 2, '西瓜汁', 50.01, 32, 1600.32, '2026-02-26 17:36:53', '');
INSERT INTO `t_order_item` VALUES (2026955455905923073, '7b5772c334a84b9ca8681a91e4327ce4', 1, '麻辣小龙虾', 78.80, 3, 236.40, '2026-02-26 17:40:14', '');
INSERT INTO `t_order_item` VALUES (2026955455973031937, '7b5772c334a84b9ca8681a91e4327ce4', 2, '西瓜汁', 50.01, 3, 150.03, '2026-02-26 17:40:14', '');
INSERT INTO `t_order_item` VALUES (2026955455973031938, '7b5772c334a84b9ca8681a91e4327ce4', 4, '肉夹馍', 0.04, 3, 0.12, '2026-02-26 17:40:14', '');
INSERT INTO `t_order_item` VALUES (2026955455973031939, '7b5772c334a84b9ca8681a91e4327ce4', 5, '佛跳墙', 10000.00, 3, 30000.00, '2026-02-26 17:40:14', '');
INSERT INTO `t_order_item` VALUES (2026955455973031940, '7b5772c334a84b9ca8681a91e4327ce4', 6, '抹茶', 0.04, 3, 0.12, '2026-02-26 17:40:14', '');
INSERT INTO `t_order_item` VALUES (2026955455973031941, '7b5772c334a84b9ca8681a91e4327ce4', 7, '哈哈', 0.02, 3, 0.06, '2026-02-26 17:40:14', '');
INSERT INTO `t_order_item` VALUES (2026960008143446018, '9c77f297a4104c3aa29b369ec838dee3', 6, '抹茶', 0.04, 12, 0.48, '2026-02-26 17:58:20', '');
INSERT INTO `t_order_item` VALUES (2026960316517064706, 'bd59b39968e54ff6abfe19af2ec11eeb', 6, '抹茶', 0.04, 1, 0.04, '2026-02-26 17:59:33', '');
INSERT INTO `t_order_item` VALUES (2026960915648225282, 'cbf15945ff5d4606825b1d5915e04b19', 5, '佛跳墙', 10000.00, 1, 10000.00, '2026-02-26 18:01:56', '');
INSERT INTO `t_order_item` VALUES (2026961054601322498, '0d2a42ee47274e6fa25d5d66a1d68198', 5, '佛跳墙', 10000.00, 1, 10000.00, '2026-02-26 18:02:29', '');
INSERT INTO `t_order_item` VALUES (2026961236961271809, '62cf9d54eb54485b9c1260a6b5359bde', 4, '肉夹馍', 0.04, 1, 0.04, '2026-02-26 18:03:13', '');
INSERT INTO `t_order_item` VALUES (2026961236961271810, '62cf9d54eb54485b9c1260a6b5359bde', 5, '佛跳墙', 10000.00, 1, 10000.00, '2026-02-26 18:03:13', '');
INSERT INTO `t_order_item` VALUES (2026961364661051394, '02c064c9b3ee4cdfbcaf7e3e65e15b37', 5, '佛跳墙', 10000.00, 1, 10000.00, '2026-02-26 18:03:43', '');
INSERT INTO `t_order_item` VALUES (2026961364661051395, '02c064c9b3ee4cdfbcaf7e3e65e15b37', 4, '肉夹馍', 0.04, 1, 0.04, '2026-02-26 18:03:43', '');
INSERT INTO `t_order_item` VALUES (2026962708499275777, '63d037e54c544dd2bd0684b313c2ad82', 4, '肉夹馍', 0.04, 3, 0.12, '2026-02-26 18:09:04', '');
INSERT INTO `t_order_item` VALUES (2026962708499275778, '63d037e54c544dd2bd0684b313c2ad82', 5, '佛跳墙', 10000.00, 5, 50000.00, '2026-02-26 18:09:04', '');
INSERT INTO `t_order_item` VALUES (2026962708499275779, '63d037e54c544dd2bd0684b313c2ad82', 2, '西瓜汁', 50.01, 1, 50.01, '2026-02-26 18:09:04', '');
INSERT INTO `t_order_item` VALUES (2026962708566384642, '63d037e54c544dd2bd0684b313c2ad82', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-02-26 18:09:04', '');
INSERT INTO `t_order_item` VALUES (2028023152130867202, 'd0f1e6b847514fd6adf1bb521077d9a5', 7, '哈哈', 0.02, 2, 0.04, '2026-03-01 16:22:53', '');
INSERT INTO `t_order_item` VALUES (2028024007827603457, '94a36bb1a33c4178bff952e37deb7de8', 5, '佛跳墙', 10000.00, 1, 10000.00, '2026-03-01 16:26:17', '');
INSERT INTO `t_order_item` VALUES (2028027779446468610, 'feed9b2e2d114c0e8f5f6f172c98709d', 7, '哈哈', 0.03, 1, 0.03, '2026-03-01 16:41:16', '');
INSERT INTO `t_order_item` VALUES (2028028016122654721, '2ede3fa03f11454ca2f9f2cfdc787704', 9, '1212', 0.02, 1, 0.02, '2026-03-01 16:42:13', '');
INSERT INTO `t_order_item` VALUES (2028414857892057090, '79175af1e6194c25947dbbd82c85441d', 2, '西瓜汁', 50.01, 1, 50.01, '2026-03-02 18:19:23', '');
INSERT INTO `t_order_item` VALUES (2028414857892057091, '79175af1e6194c25947dbbd82c85441d', 7, '哈哈', 0.03, 1, 0.03, '2026-03-02 18:19:23', '');
INSERT INTO `t_order_item` VALUES (2028414857892057092, '79175af1e6194c25947dbbd82c85441d', 3, '生椰拿铁', 20.00, 1, 20.00, '2026-03-02 18:19:23', '');
INSERT INTO `t_order_item` VALUES (2028428775473418241, 'e66495e438724b2d8a377b4970787fab', 1, '麻辣小龙虾', 78.80, 1, 78.80, '2026-03-02 19:14:41', '');
INSERT INTO `t_order_item` VALUES (2028428775473418242, 'e66495e438724b2d8a377b4970787fab', 2, '西瓜汁', 50.01, 1, 50.01, '2026-03-02 19:14:41', '');

-- ----------------------------
-- Table structure for t_shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `t_shopping_cart`;
CREATE TABLE `t_shopping_cart`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，关联t_user.id',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品ID，关联t_dish.id',
  `merchant_id` bigint(20) NOT NULL COMMENT '商家ID，关联t_dish.merchant_id',
  `count` int(11) NOT NULL DEFAULT 1 COMMENT '购买数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `dish_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜品名称',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '菜品价格',
  `cover_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜品图片URL',
  `valid_unique_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS ((case when (`is_deleted` = 0) then concat(`user_id`,_utf8mb4'_',`dish_id`) else NULL end)) STORED NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_dish_valid`(`valid_unique_key` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_dish_id`(`dish_id` ASC) USING BTREE,
  INDEX `idx_merchant_id`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_shopping_cart
-- ----------------------------
INSERT INTO `t_shopping_cart` VALUES (4, 1, 3, 7, 1, '2026-02-06 16:29:59', '2026-02-07 20:47:26', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (5, 1, 3, 7, 1, '2026-02-07 20:58:44', '2026-02-07 21:22:59', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (6, 1, 3, 7, 1, '2026-02-07 21:27:56', '2026-02-07 21:28:27', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (7, 1, 3, 7, 1, '2026-02-07 21:33:07', '2026-02-07 21:34:01', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (8, 1, 3, 7, 1, '2026-02-07 21:35:52', '2026-02-07 21:36:24', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (9, 1, 3, 7, 1, '2026-02-07 21:45:54', '2026-02-07 21:46:23', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (10, 1, 3, 7, 1, '2026-02-07 21:50:00', '2026-02-07 21:50:41', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (11, 1, 3, 7, 1, '2026-02-07 21:54:09', '2026-02-07 21:54:36', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (12, 1, 3, 7, 1, '2026-02-07 23:48:09', '2026-02-07 23:48:32', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (13, 1, 3, 7, 1, '2026-02-07 23:53:47', '2026-02-07 23:54:18', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (14, 1, 3, 7, 1, '2026-02-08 00:00:52', '2026-02-08 00:02:55', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (15, 1, 3, 7, 1, '2026-02-08 00:06:25', '2026-02-08 00:07:03', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (16, 1, 3, 7, 1, '2026-02-08 00:08:27', '2026-02-08 00:09:51', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (17, 1, 3, 7, 1, '2026-02-08 00:10:59', '2026-02-08 00:11:25', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (18, 1, 3, 7, 1, '2026-02-08 00:13:08', '2026-02-08 00:13:25', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (19, 1, 3, 7, 1, '2026-02-08 00:21:17', '2026-02-08 00:21:38', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (20, 1, 3, 7, 1, '2026-02-08 00:24:18', '2026-02-08 00:24:54', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (21, 1, 3, 7, 1, '2026-02-08 00:27:15', '2026-02-08 00:30:28', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (22, 1, 3, 7, 1, '2026-02-08 00:40:23', '2026-02-08 00:40:39', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (23, 1, 3, 7, 1, '2026-02-08 00:40:53', '2026-02-08 00:41:17', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (24, 1, 3, 7, 1, '2026-02-08 01:01:41', '2026-02-08 01:02:24', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (25, 1, 3, 7, 1, '2026-02-08 01:02:34', '2026-02-08 01:02:51', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (26, 1, 3, 7, 1, '2026-02-08 13:55:34', '2026-02-08 13:55:58', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (27, 1, 3, 7, 1, '2026-02-08 14:45:11', '2026-02-08 14:45:38', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (28, 1, 3, 7, 1, '2026-02-08 14:55:43', '2026-02-08 14:56:00', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (29, 1, 3, 7, 1, '2026-02-08 15:04:44', '2026-02-08 15:05:02', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (30, 2, 3, 7, 1, '2026-02-08 15:18:10', '2026-02-08 15:18:27', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (31, 2, 3, 7, 1, '2026-02-08 17:45:20', '2026-02-08 17:45:34', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (32, 2, 3, 7, 1, '2026-02-08 18:03:02', '2026-02-08 18:03:20', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (33, 2, 3, 7, 1, '2026-02-08 18:56:46', '2026-02-08 18:58:11', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (34, 2, 3, 7, 1, '2026-02-08 21:15:36', '2026-02-08 21:16:45', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (35, 2, 2, 7, 1, '2026-02-08 21:16:25', '2026-02-08 21:16:45', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (36, 2, 2, 7, 10, '2026-02-08 21:19:35', '2026-02-08 21:21:05', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (37, 2, 3, 7, 1, '2026-02-08 21:31:46', '2026-02-08 21:32:04', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (38, 2, 3, 7, 1, '2026-02-08 21:39:42', '2026-02-08 21:39:58', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (39, 2, 2, 7, 3, '2026-02-08 21:47:00', '2026-02-14 20:32:23', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (40, 2, 3, 7, 2, '2026-02-08 21:54:24', '2026-02-14 20:32:23', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (41, 2, 3, 7, 1, '2026-02-14 20:35:15', '2026-02-23 15:17:10', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (42, 2, 2, 7, 1, '2026-02-23 15:34:10', '2026-02-23 15:34:34', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (43, 2, 2, 7, 1, '2026-02-23 15:36:58', '2026-02-23 15:37:19', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (44, 2, 2, 7, 1, '2026-02-23 15:44:08', '2026-02-23 15:44:28', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (45, 2, 2, 7, 1, '2026-02-23 17:53:33', '2026-02-23 17:53:55', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (46, 2, 2, 7, 2, '2026-02-23 18:32:11', '2026-02-25 22:13:39', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (47, 2, 3, 7, 1, '2026-02-25 22:44:31', '2026-02-25 22:44:51', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (48, 2, 3, 7, 2, '2026-02-25 22:49:52', '2026-02-25 22:50:19', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (49, 2, 2, 7, 2, '2026-02-25 22:49:54', '2026-02-25 22:50:19', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (50, 2, 3, 7, 1, '2026-02-25 23:20:53', '2026-02-25 23:21:10', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (51, 2, 2, 7, 1, '2026-02-26 16:12:13', '2026-02-26 16:15:18', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (52, 2, 3, 7, 3, '2026-02-26 16:12:15', '2026-02-26 16:15:18', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (53, 2, 3, 7, 2, '2026-02-26 16:16:30', '2026-02-26 16:16:48', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (54, 2, 2, 7, 2, '2026-02-26 16:16:31', '2026-02-26 16:16:48', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (55, 2, 3, 7, 1, '2026-02-26 16:18:36', '2026-02-26 18:09:04', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (56, 2, 2, 7, 1, '2026-02-26 16:18:43', '2026-02-26 18:09:04', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (57, 4, 2, 7, 21, '2026-02-26 17:33:52', '2026-02-26 17:35:09', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (58, 4, 2, 7, 32, '2026-02-26 17:36:32', '2026-02-26 17:36:53', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (59, 4, 7, 7, 3, '2026-02-26 17:39:32', '2026-02-26 17:40:14', 1, '哈哈', 0.02, 'http://127.0.0.1:8080/uploads/80101de858b04cfc904f68c24500fc4d_1772093994357_291.jpg', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (60, 4, 6, 7, 3, '2026-02-26 17:39:33', '2026-02-26 17:40:14', 1, '抹茶', 0.04, 'http://localhost:8080/uploads/f896ad21fa0f4b6fa8812a3aa2f90d85_1770352997676_142.webp', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (61, 4, 5, 7, 3, '2026-02-26 17:39:34', '2026-02-26 17:40:14', 1, '佛跳墙', 10000.00, '', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (62, 4, 4, 7, 3, '2026-02-26 17:39:35', '2026-02-26 17:40:14', 1, '肉夹馍', 0.04, 'http://localhost:8080/uploads/4fc23ec8ddef4d24835ed4543eeadf0c_1769781794011_276.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (63, 4, 2, 7, 3, '2026-02-26 17:39:36', '2026-02-26 17:40:14', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (64, 4, 1, 7, 3, '2026-02-26 17:39:37', '2026-02-26 17:40:14', 1, '麻辣小龙虾', 78.80, 'http://localhost:8080/uploads/ebee374be0ab4592829ca2112b0dabac_1769781803323_26.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (65, 4, 6, 7, 12, '2026-02-26 17:58:03', '2026-02-26 17:58:20', 1, '抹茶', 0.04, 'http://localhost:8080/uploads/f896ad21fa0f4b6fa8812a3aa2f90d85_1770352997676_142.webp', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (66, 4, 6, 7, 1, '2026-02-26 17:59:19', '2026-02-26 17:59:33', 1, '抹茶', 0.04, 'http://localhost:8080/uploads/f896ad21fa0f4b6fa8812a3aa2f90d85_1770352997676_142.webp', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (67, 4, 5, 7, 1, '2026-02-26 18:01:41', '2026-02-26 18:01:56', 1, '佛跳墙', 10000.00, '', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (68, 4, 5, 7, 1, '2026-02-26 18:02:14', '2026-02-26 18:02:29', 1, '佛跳墙', 10000.00, '', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (69, 4, 5, 7, 1, '2026-02-26 18:02:56', '2026-02-26 18:03:13', 1, '佛跳墙', 10000.00, '', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (70, 4, 4, 7, 1, '2026-02-26 18:02:57', '2026-02-26 18:03:13', 1, '肉夹馍', 0.04, 'http://localhost:8080/uploads/4fc23ec8ddef4d24835ed4543eeadf0c_1769781794011_276.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (71, 4, 4, 7, 1, '2026-02-26 18:03:26', '2026-02-26 18:03:43', 1, '肉夹馍', 0.04, 'http://localhost:8080/uploads/4fc23ec8ddef4d24835ed4543eeadf0c_1769781794011_276.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (72, 4, 5, 7, 1, '2026-02-26 18:03:27', '2026-02-26 18:03:43', 1, '佛跳墙', 10000.00, '', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (73, 2, 5, 7, 5, '2026-02-26 18:08:38', '2026-02-26 18:09:04', 1, '佛跳墙', 10000.00, '', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (74, 2, 4, 7, 3, '2026-02-26 18:08:40', '2026-02-26 18:09:04', 1, '肉夹馍', 0.04, 'http://localhost:8080/uploads/4fc23ec8ddef4d24835ed4543eeadf0c_1769781794011_276.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (75, 5, 7, 7, 2, '2026-03-01 16:21:56', '2026-03-01 16:22:53', 1, '哈哈', 0.02, 'http://127.0.0.1:8080/uploads/80101de858b04cfc904f68c24500fc4d_1772093994357_291.jpg', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (76, 5, 5, 7, 1, '2026-03-01 16:26:09', '2026-03-01 16:26:17', 1, '佛跳墙', 10000.00, '', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (77, 5, 7, 7, 1, '2026-03-01 16:41:05', '2026-03-01 16:41:16', 1, '哈哈', 0.03, 'http://127.0.0.1:8080/uploads/f250dda459fa4e1e9a6b63eda1ddd682_1772353554643_690.jpg', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (78, 5, 9, 7, 1, '2026-03-01 16:41:53', '2026-03-01 16:42:13', 1, '1212', 0.02, 'http://127.0.0.1:8080/uploads/2be3e3edf3c5471da970f9f788786f2b_1772354394217_597.jpg', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (79, 5, 3, 7, 1, '2026-03-01 16:53:17', '2026-03-02 18:19:23', 1, '生椰拿铁', 20.00, 'http://localhost:8080/uploads/dddd1f8db9bb494596ce0b2b6fdb74d1_1769781811682_194.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (80, 5, 7, 7, 1, '2026-03-01 16:53:21', '2026-03-02 18:19:23', 1, '哈哈', 0.03, 'http://127.0.0.1:8080/uploads/f250dda459fa4e1e9a6b63eda1ddd682_1772353554643_690.jpg', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (81, 5, 2, 7, 1, '2026-03-02 18:19:12', '2026-03-02 18:19:23', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (82, 5, 2, 7, 1, '2026-03-02 19:14:29', '2026-03-02 19:14:41', 1, '西瓜汁', 50.01, 'http://localhost:8080/uploads/f0ad1d12c03b4998bd8ec2db70d32aca_1769781817162_806.JPG', DEFAULT);
INSERT INTO `t_shopping_cart` VALUES (83, 5, 1, 7, 1, '2026-03-02 19:14:30', '2026-03-02 19:14:41', 1, '麻辣小龙虾', 78.80, 'http://localhost:8080/uploads/ebee374be0ab4592829ca2112b0dabac_1769781803323_26.JPG', DEFAULT);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID（主键）',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '匿名用户' COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户头像URL',
  `signature` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个性签名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '信息更新时间',
  `is_valid` tinyint(1) NULL DEFAULT 1 COMMENT '账号状态（1-正常，0-禁用）',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户电话',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_openid`(`openid` ASC) USING BTREE COMMENT '微信openid唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '微信用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, NULL, '小丽', '', '', '2026-01-30 13:58:42', '2026-02-01 17:58:53', 1, '张三', '10086');
INSERT INTO `t_user` VALUES (2, 'oj9766fYEsYA8EX4iuaWnM5iYoGs', '时光飞逝大公司', '', '很符合防护服', '2026-02-03 18:40:05', '2026-02-26 16:19:17', 1, NULL, NULL);
INSERT INTO `t_user` VALUES (3, 'oj9766bv8J495mIjqy8_6u2murJI', '匿名用户', '', '', '2026-02-26 17:28:50', '2026-02-26 17:28:50', 1, NULL, NULL);
INSERT INTO `t_user` VALUES (4, 'oj9766WX4QYn961qf1AbV0DWEpYM', '匿名用户', '', '', '2026-02-26 17:29:28', '2026-02-26 17:36:05', 1, NULL, NULL);
INSERT INTO `t_user` VALUES (5, 'h5_1772353137480_6afab56e77b9a', '匿名用户', '', '', '2026-03-01 16:21:52', '2026-03-01 16:21:52', 1, NULL, NULL);
INSERT INTO `t_user` VALUES (6, 'h5_test123', '匿名用户', '', '', '2026-03-01 16:26:29', '2026-03-01 16:26:29', 1, NULL, NULL);
INSERT INTO `t_user` VALUES (7, 'h5_1772353199194_ea2795a4fc8528', '匿名用户', '', '', '2026-03-01 16:26:50', '2026-03-01 16:26:50', 1, NULL, NULL);
INSERT INTO `t_user` VALUES (8, 'h5_1772381740691_fdec657922591', '匿名用户', '', '', '2026-03-02 00:15:41', '2026-03-02 00:15:41', 1, NULL, NULL);

-- ----------------------------
-- Table structure for t_user_behavior
-- ----------------------------
DROP TABLE IF EXISTS `t_user_behavior`;
CREATE TABLE `t_user_behavior`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '行为记录ID（主键）',
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `dish_id` bigint(20) NOT NULL COMMENT '关联菜品ID',
  `behavior_type` tinyint(1) NOT NULL COMMENT '行为类型（1-浏览，2-收藏，3-加购，4-下单）',
  `behavior_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间',
  `duration` int(11) NULL DEFAULT 0 COMMENT '浏览时长（秒，仅行为类型1有效）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户行为统计索引',
  INDEX `idx_dish_id`(`dish_id` ASC) USING BTREE COMMENT '菜品被操作统计索引',
  INDEX `idx_behavior_time`(`behavior_time` ASC) USING BTREE COMMENT '时间范围行为统计'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为表（用于智能菜品推荐）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_behavior
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
