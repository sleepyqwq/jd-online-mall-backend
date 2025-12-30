/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80043
 Source Host           : localhost:3306
 Source Schema         : jd_online_mall

 Target Server Type    : MySQL
 Target Server Version : 80043
 File Encoding         : 65001

 Date: 29/12/2025 17:18:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_address
-- ----------------------------
DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '省',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '市',
  `district` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '区',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认：0否1是',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_address
-- ----------------------------
INSERT INTO `t_address` VALUES (1, 4, '贞子', '11122223333', '安徽', '马鞍山', '雨山区', '不嘻嘻镇', 1, '2025-12-15 16:59:53', '2025-12-15 17:01:02', 1);
INSERT INTO `t_address` VALUES (2, 4, '贞子', '11122223333', '安徽', '马鞍山', '雨山区', '新市镇', 1, '2025-12-15 17:02:55', '2025-12-15 17:02:55', 0);
INSERT INTO `t_address` VALUES (3, 5, '教官', '11122225555', '安徽', '马鞍山', '博望区', '新博', 1, '2025-12-16 22:38:13', '2025-12-16 22:38:13', 0);
INSERT INTO `t_address` VALUES (4, 2, '陶聪', '11155553333', '安徽', '当涂', '朝阳区', '九龙路', 1, '2025-12-23 21:59:50', '2025-12-23 21:59:50', 0);

-- ----------------------------
-- Table structure for t_banner
-- ----------------------------
DROP TABLE IF EXISTS `t_banner`;
CREATE TABLE `t_banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `redirect_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接(可选)',
  `sort_order` int NULL DEFAULT 1 COMMENT '排序(越小越前)',
  `status` int NULL DEFAULT 1 COMMENT '状态:1启用,0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` int NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '首页轮播图' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_banner
-- ----------------------------
INSERT INTO `t_banner` VALUES (3, '/images/2025/12/16/43f423cae9fc40a3af73c7c8fe441b34.jpg', '/products/5', 3, 1, '2025-12-16 15:43:13', NULL, 1);
INSERT INTO `t_banner` VALUES (4, '/images/2025/12/16/8a4fdab425d1433f8d217234dd1be33c.jpg', '/products/7', 2, 1, '2025-12-16 15:49:10', NULL, 1);
INSERT INTO `t_banner` VALUES (5, '/images/2025/12/22/f9c59d36f6644214adce8722677827fc.jpg', '/products/9', 5, 1, '2025-12-16 15:52:44', '2025-12-22 22:12:39', 0);
INSERT INTO `t_banner` VALUES (6, '/images/2025/12/22/cdfddf7d3f1e47b9a7ce4eab3b03afc5.jpg', '/products/12', 2, 1, '2025-12-16 16:07:14', '2025-12-22 19:53:17', 0);
INSERT INTO `t_banner` VALUES (7, '/images/2025/12/22/9ecb9af4bc4146e9bbc0441d2778a039.jpg', '/products/14', 3, 1, '2025-12-22 19:53:14', '2025-12-22 19:53:14', 0);
INSERT INTO `t_banner` VALUES (8, '/images/2025/12/22/f7ff0c700b4a48f5b892da99c5db5bfd.jpg', '/products/5', 4, 1, '2025-12-22 20:44:36', '2025-12-22 20:44:36', 0);
INSERT INTO `t_banner` VALUES (9, '/images/2025/12/22/924bbc4eb0bf4dbc96d8df1ae3bccbb9.jpeg', '/products/16', 1, 1, '2025-12-22 22:12:03', '2025-12-22 22:12:44', 0);

-- ----------------------------
-- Table structure for t_cart_item
-- ----------------------------
DROP TABLE IF EXISTS `t_cart_item`;
CREATE TABLE `t_cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '数量',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2001652151273250817 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物车条目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_cart_item
-- ----------------------------
INSERT INTO `t_cart_item` VALUES (2000820110671536129, 4, 8, 1, '2025-12-16 14:47:42', '2025-12-16 14:47:42', 1);
INSERT INTO `t_cart_item` VALUES (2000820122923098113, 4, 9, 1, '2025-12-16 14:47:45', '2025-12-17 14:41:29', 1);
INSERT INTO `t_cart_item` VALUES (2000821310041153538, 4, 6, 1, '2025-12-16 14:52:28', '2025-12-16 14:52:28', 1);
INSERT INTO `t_cart_item` VALUES (2000883084878684161, 4, 12, 1, '2025-12-16 18:57:57', '2025-12-17 14:41:26', 1);
INSERT INTO `t_cart_item` VALUES (2000938265687388161, 5, 8, 10, '2025-12-16 22:37:13', '2025-12-16 22:37:13', 1);
INSERT INTO `t_cart_item` VALUES (2000939050752045057, 5, 13, 1, '2025-12-16 22:40:20', '2025-12-16 22:40:20', 1);
INSERT INTO `t_cart_item` VALUES (2001180948058144770, 4, 7, 1, '2025-12-17 14:41:33', '2025-12-17 14:41:33', 1);
INSERT INTO `t_cart_item` VALUES (2001652151273250817, 4, 13, 1, '2025-12-18 21:53:56', '2025-12-18 21:53:56', 1);

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID，0 表示一级分类',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0 未删 1 已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_category
-- ----------------------------
INSERT INTO `t_category` VALUES (1, '手机数码', 0, 1, '2025-12-12 19:02:19', '2025-12-12 19:02:19', 0);
INSERT INTO `t_category` VALUES (2, '手机', 1, 1, '2025-12-12 19:03:38', '2025-12-12 19:03:38', 0);
INSERT INTO `t_category` VALUES (3, '电脑', 0, 1, '2025-12-12 20:37:21', '2025-12-12 20:37:21', 0);
INSERT INTO `t_category` VALUES (4, '笔记本', 3, 1, '2025-12-14 18:14:47', '2025-12-15 21:10:26', 0);
INSERT INTO `t_category` VALUES (5, '手柄', 0, 1, '2025-12-14 18:20:50', '2025-12-16 16:01:41', 1);
INSERT INTO `t_category` VALUES (6, 'xbox', 1, 1, '2025-12-14 18:21:52', '2025-12-16 16:01:34', 0);
INSERT INTO `t_category` VALUES (7, '衣服', 0, 1, '2025-12-15 20:54:12', '2025-12-15 20:54:12', 0);
INSERT INTO `t_category` VALUES (8, 'jk', 7, 1, '2025-12-15 20:54:49', '2025-12-15 20:56:01', 0);
INSERT INTO `t_category` VALUES (9, '游戏机', 1, 1, '2025-12-15 20:58:55', '2025-12-15 20:58:55', 0);
INSERT INTO `t_category` VALUES (10, '丝袜', 7, 1, '2025-12-15 21:02:09', '2025-12-15 21:02:09', 0);
INSERT INTO `t_category` VALUES (11, '显卡', 3, 1, '2025-12-15 21:23:51', '2025-12-15 21:23:51', 0);
INSERT INTO `t_category` VALUES (12, '内存条', 3, 1, '2025-12-15 21:26:49', '2025-12-15 21:43:45', 1);
INSERT INTO `t_category` VALUES (13, '摄影机', 1, 1, '2025-12-16 15:54:16', '2025-12-16 15:54:16', 0);
INSERT INTO `t_category` VALUES (14, '硬盘', 3, 1, '2025-12-16 15:57:45', '2025-12-16 15:57:45', 0);
INSERT INTO `t_category` VALUES (15, '食品', 0, 1, '2025-12-16 16:02:25', '2025-12-16 16:02:25', 0);
INSERT INTO `t_category` VALUES (16, '零食', 15, 1, '2025-12-16 16:02:36', '2025-12-16 16:02:36', 0);
INSERT INTO `t_category` VALUES (17, '水果', 15, 1, '2025-12-16 16:02:44', '2025-12-16 16:02:44', 0);
INSERT INTO `t_category` VALUES (18, '轻奢', 0, 1, '2025-12-22 19:46:44', '2025-12-22 19:46:44', 0);
INSERT INTO `t_category` VALUES (19, '包包', 18, 1, '2025-12-22 19:46:59', '2025-12-22 19:46:59', 0);
INSERT INTO `t_category` VALUES (20, '首饰', 18, 1, '2025-12-22 19:57:10', '2025-12-22 19:57:10', 0);
INSERT INTO `t_category` VALUES (21, '游戏卡带', 1, 1, '2025-12-22 22:05:17', '2025-12-22 22:05:17', 0);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态：WAIT_PAY/PAID/SHIPPED/COMPLETED/CANCELED',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总额',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `receiver_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址快照-收货人',
  `receiver_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址快照-电话',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址快照-省',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址快照-市',
  `district` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址快照-区',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址快照-详细地址',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '取消原因：USER_CANCEL/SYSTEM_TIMEOUT',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_status_ct`(`user_id` ASC, `status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (4, '2025121600000004', 4, 'CANCELED', 30918.00, '要新鲜的', '贞子', '11122223333', '安徽', '马鞍山', '雨山区', '新市镇', NULL, '2025-12-16 14:49:02', '不想要了', '2025-12-16 15:03:01', '2025-12-16 14:48:01', '2025-12-16 14:49:02', 0);
INSERT INTO `t_order` VALUES (5, '2025121600000005', 4, 'CANCELED', 3333.00, '呃呃呃呃啊啊啊！我要玩塞尔达啊啊啊！！', '贞子', '11122223333', '安徽', '马鞍山', '雨山区', '新市镇', NULL, '2025-12-16 15:13:33', 'SYSTEM_TIMEOUT', '2025-12-16 15:13:27', '2025-12-16 14:58:27', '2025-12-16 15:13:33', 0);
INSERT INTO `t_order` VALUES (6, '2025121600000006', 5, 'COMPLETED', 919.00, '已收货', '教官', '11122225555', '安徽', '马鞍山', '博望区', '新博', '2025-12-16 22:39:25', NULL, NULL, '2025-12-16 22:53:38', '2025-12-16 22:38:38', '2025-12-16 22:39:39', 0);

-- ----------------------------
-- Table structure for t_order_item
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品标题快照',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品图片快照(可存相对路径)',
  `price` decimal(10, 2) NOT NULL COMMENT '下单单价快照',
  `quantity` int NOT NULL COMMENT '数量',
  `subtotal_amount` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_item
-- ----------------------------
INSERT INTO `t_order_item` VALUES (4, 4, 8, '黑丝', '/images/2025/12/15/b35e27aabe504ea38881cc97155e6cca.webp', 919.00, 1, 919.00, '2025-12-16 14:48:01', '2025-12-16 14:48:01', 0);
INSERT INTO `t_order_item` VALUES (5, 4, 9, 'RTX5090', '/images/2025/12/15/01d40ac94a55445daa68bd1b777e103d.jpg', 29999.00, 1, 29999.00, '2025-12-16 14:48:01', '2025-12-16 14:48:01', 0);
INSERT INTO `t_order_item` VALUES (6, 5, 6, 'switch游戏机', '/images/2025/12/15/1a3fbba4deb64e9c925a1e82b281ea83.jpg', 3333.00, 1, 3333.00, '2025-12-16 14:58:27', '2025-12-16 14:58:27', 0);
INSERT INTO `t_order_item` VALUES (7, 6, 8, '黑丝', '/images/2025/12/15/b35e27aabe504ea38881cc97155e6cca.webp', 91.90, 10, 919.00, '2025-12-16 22:38:38', '2025-12-16 22:38:38', 0);

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品标题',
  `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品详情',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主图URL',
  `image_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图片列表，逗号分隔',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ON_SHELF' COMMENT '状态：ON_SHELF/OFF_SHELF',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES (1, '菠萝牌手机', '八核处理器 8G 内存', '只要888，只要888，菠萝手机带回家', 888.00, 100, 2, '/images/2025/12/16/e3a0fae64c0f4aab96f3f898260a4132.jpg', '/images/2025/12/15/746ae0ceb3a446318abf1cf75f1b9f24.jpg,/images/2025/12/15/12af69042edb4761bd9402996213e7bd.jpg', 'ON_SHELF', '2025-12-12 20:42:06', '2025-12-16 14:47:04', 0);
INSERT INTO `t_product` VALUES (2, 'vivo手机', '巨出片，出巨片！', '正在搜索簧片...', 5555.00, 53, 2, '/images/2025/12/15/63e3eeccc532496997b04bb21435f88d.jpg', '/images/2025/12/15/10fd03bc64704b0cba82b2e72016535f.jpg,/images/2025/12/15/fe19653f6a9d4bb4a0e813047c3a0004.jpg', 'ON_SHELF', '2025-12-14 18:49:07', '2025-12-15 22:14:10', 0);
INSERT INTO `t_product` VALUES (3, '戴尔/外星人', '笔记本中的元神，你值得拥有！', '请支持外星人喵，谢谢喵~', 64800.00, 646, 4, '/images/2025/12/15/30214a0431d04e7f9a58630d1630fed8.avif', '/images/2025/12/15/a2cc1669379941b4beddaaceced8124f.avif,/images/2025/12/15/d4403fa84da04a15b5f5a1e3bd39c070.png', 'ON_SHELF', '2025-12-14 18:52:48', '2025-12-15 22:13:58', 0);
INSERT INTO `t_product` VALUES (4, 'xbox series', '凡是任天堂支持的我就反对', '请注意！A键就应该在下面！请无视其他手柄', 457.00, 100, 6, '/images/2025/12/15/ef91c722b02c4622915c6e73c37ec685.jpg', '/images/2025/12/15/067afd8c257a4ebeb44b1571f88b7bf4.jpg,/images/2025/12/15/1b836aba5a5f445587f5ca10598e6aac.png', 'ON_SHELF', '2025-12-15 20:52:17', '2025-12-15 20:53:07', 0);
INSERT INTO `t_product` VALUES (5, '纯爱百褶裙', '可爱o(*￣▽￣*)ブ', '没有人会拒绝jk~😊', 555.00, 55, 8, '/images/2025/12/15/422982328358419e98a61eb5450b6c96.jpeg', '/images/2025/12/15/f28b90343e4c44189c61e550f274e987.jpeg,/images/2025/12/15/915edcb501c9459493bcd00def487b90.jpg,/images/2025/12/22/0ab8bf7e25ec4db48c94ab89eab1504b.jpg,/images/2025/12/22/e34cbe5e411e41299693a25056d3223b.png,/images/2025/12/22/8035d4fe75654e65b2167de6f0d1a5f3.jpeg,/images/2025/12/22/9e35f4cf8cde4741bbfd42c3394e82b5.avif', 'ON_SHELF', '2025-12-15 20:58:33', '2025-12-22 19:25:32', 0);
INSERT INTO `t_product` VALUES (6, 'switch游戏机', 'switch1代', '我是塞尔达，v我3333，封你做下一任勇者😊', 3333.00, 333, 9, '/images/2025/12/15/1a3fbba4deb64e9c925a1e82b281ea83.jpg', '/images/2025/12/15/3a7c02b071954da09bbeff2a8dad3dfc.jpg,/images/2025/12/15/8722bf35ece24208b72159d01000804d.jpg', 'ON_SHELF', '2025-12-15 21:01:26', '2025-12-16 15:13:33', 0);
INSERT INTO `t_product` VALUES (7, '白丝', '纯白、可爱', '白丝天下第一！不服来辩！', 91.00, 91, 10, '/images/2025/12/15/2a973d43fec04e869e1767b4b4320051.jpg', '/images/2025/12/15/0cca51cec9d045c59a58b8d338f1ff24.jpg,/images/2025/12/15/e15bf988170741d9b6b2d1293c966767.jpg', 'ON_SHELF', '2025-12-15 21:07:03', '2025-12-15 21:28:58', 0);
INSERT INTO `t_product` VALUES (8, '黑丝', '性感、厚黑', '真的有人会质疑黑丝？😍', 91.90, 121, 10, '/images/2025/12/15/b35e27aabe504ea38881cc97155e6cca.webp', '/images/2025/12/15/badaf2f1f4be4ada98f813dbfded92b9.webp,/images/2025/12/15/d4eba997605d4634b05cf9ebe03ea96c.jpg', 'ON_SHELF', '2025-12-15 21:09:03', '2025-12-16 22:38:37', 0);
INSERT INTO `t_product` VALUES (9, 'RTX5090', '双风扇设计、性能max', '黄氏骗局（划掉），AI才是未来！', 29999.00, 5, 11, '/images/2025/12/15/01d40ac94a55445daa68bd1b777e103d.jpg', '/images/2025/12/15/bf0eceab492e41ef8af5bcd6680605b9.png,/images/2025/12/15/fa37dfed4131423f98584a8baf161b76.jpg,/images/2025/12/22/cf7cd924b21b4a3a880a143ebe4cf097.jpg', 'ON_SHELF', '2025-12-15 22:12:06', '2025-12-22 20:37:40', 0);
INSERT INTO `t_product` VALUES (10, '超级摄影机', '超清4K、高速捕捉', '有的人头上真的长了摄影机。。。', 2999.00, 45, 13, '/images/2025/12/16/7e595d4978924c9da717149fb1f67d75.jpg', '/images/2025/12/16/187fe5875d694caba7e565b20e84e8a1.jpg,/images/2025/12/16/d57cff1c179640f3b547f1cdda28c59f.png', 'ON_SHELF', '2025-12-16 15:56:36', '2025-12-16 15:56:36', 0);
INSERT INTO `t_product` VALUES (11, '三星固态硬盘', 'ssd、M2', '再不买还得涨bro', 999.00, 232, 14, '/images/2025/12/16/789ce242c9f24d9886454b73bd203e9f.avif', '/images/2025/12/16/c6153b444e634388b62bc4b5fffa376a.avif,/images/2025/12/16/3f15c867dc884fa683c86f29857e574a.avif', 'ON_SHELF', '2025-12-16 16:00:04', '2025-12-16 16:00:04', 0);
INSERT INTO `t_product` VALUES (12, '海外零食', '爆款！日式零食、英式零食', '哈基米美食！当年令哈基米欲哈不能的美味！', 9.90, 2313, 16, '/images/2025/12/16/9b9578c48c8b4495a2f688b3d3a490d3.jpg', '/images/2025/12/16/8254f63e8884466886b99c3d0b24b480.jpg,/images/2025/12/16/c1eab71d5e764449b47e90fd3dd8957d.jpg,/images/2025/12/22/941c21653d77486c9a876ef7908c1000.webp,/images/2025/12/22/f6e9680b035e4ce483356e69b7013396.png', 'ON_SHELF', '2025-12-16 16:05:55', '2025-12-22 20:40:58', 0);
INSERT INTO `t_product` VALUES (13, '好吃水果', '好吃😋', '陆小果强烈推荐！水果中的特工！', 23.50, 3131, 17, '/images/2025/12/16/5a741f6a48d9423982d1b86206f81bca.jpg', '/images/2025/12/16/5bb1b321cbd24839b3e55d73d764cb0d.jpg,/images/2025/12/16/e4f0296eb7234c8eb69f3e49500e4300.webp', 'ON_SHELF', '2025-12-16 16:10:39', '2025-12-16 16:10:39', 0);
INSERT INTO `t_product` VALUES (14, 'LOUIS VUITTON', '女士手提包', '爱她，就选LV(❤ ω ❤)', 29999.00, 99, 19, '/images/2025/12/22/6393fd5bcbcc46f2a90a3ebc8ad6c84b.webp', '/images/2025/12/22/14816005256e4e54b0e0e1395a4550d5.webp,/images/2025/12/22/2aaef90f624d45dbbdc241c2eca05874.webp,/images/2025/12/22/7c14e38cae664b539129f5cc486a2db4.avif', 'ON_SHELF', '2025-12-22 19:52:14', '2025-12-22 20:39:45', 0);
INSERT INTO `t_product` VALUES (15, 'BUCCELLATI', 'OPERA TULLE', 'OPERA TULLE YELLO AND White GOLD RAGGIERA BRACELET\n\nOpera Tulle, gold pendant with mother of pearl Buccellati', 38888.88, 88, 20, '/images/2025/12/22/069f623bd6814c2483f3283f57a9bc52.webp', '/images/2025/12/22/0b58f4c295124623894d05f5d938e0db.webp,/images/2025/12/22/6b7b6b534bdb4103b2d9796f52bb9e28.webp', 'ON_SHELF', '2025-12-22 20:04:13', '2025-12-22 20:04:42', 0);
INSERT INTO `t_product` VALUES (16, '冬促游戏大甩卖', '塞尔达、宝可梦、刺客信条...', '请享受属于你的Game Time😊', 98.00, 74923, 21, '/images/2025/12/22/e01c7a50e52c4a7a8acac9b55797f90f.jpg', '/images/2025/12/22/120283d0cfbd418182afb3f316a10bf8.jpg,/images/2025/12/22/d89624dfc11441a9853824c5030bcff3.jpg,/images/2025/12/22/6e52546998b9454f8c7e2872a5d7fca3.jpg,/images/2025/12/22/4ac2197ebbf943c8b74b16094a335cdb.jpg', 'ON_SHELF', '2025-12-22 22:09:13', '2025-12-22 22:09:13', 0);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号，唯一',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'BCrypt 加密后的密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址（建议存相对url）',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '角色：USER / ADMIN',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0未删 1已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'admin', '$2b$10$NRNWPNpYqLtdxfF7VinYKei1cpG2/J114Vi16QzlS3LGRzj8s6OqC', 'hkqwq', '/images/猎人.jpg', 'ADMIN', '2025-12-11 21:04:58', '2025-12-15 20:37:04', 0);
INSERT INTO `t_user` VALUES (2, '陶葱', '$2a$10$KQtsupFnVPyxjOVJsXAlJeW/3PRqyl/mQYn4l4ytfCSy8TJQWNLFi', '无处可陶(搞毛)', '/images/avatar01.jpg', 'USER', '2025-12-11 21:10:53', '2025-12-11 21:10:53', 0);
INSERT INTO `t_user` VALUES (3, '许纯一', '$2a$10$0ixaUQBtamyDFQEXMV/esukOy8.YrWC6Lh3/XsBRbSULljTgG1t66', '李辞盈', '/images/avatar02.jpg', 'USER', '2025-12-12 17:18:22', '2025-12-15 20:40:00', 0);
INSERT INTO `t_user` VALUES (4, '贞子', '$2a$10$.2dixBr8jXkpm69hG8Z/E.0QU8P1mpANhsSUhnkSn7PHFPV52p05G', 'Uraykevoli', '/images/avatar03.jpg', 'USER', '2025-12-12 17:23:59', '2025-12-12 17:23:59', 0);
INSERT INTO `t_user` VALUES (5, '教官', '$2a$10$aGXyTCnNhR3CS5bsgPfaeuzcs6trBPrwSPlbJttZrix7OWcygkViO', '盛至杯缘', '/images/avatar04.jpg', 'USER', '2025-12-16 21:56:16', '2025-12-16 21:57:10', 0);

SET FOREIGN_KEY_CHECKS = 1;
