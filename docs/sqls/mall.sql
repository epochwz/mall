DROP DATABASE IF EXISTS `mall`;

CREATE DATABASE `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# collation: 指定字符串排序规则和比较规则
#
# utf8mb4_unicode_ci: 基于标准的 Unicode 来排序和比较，能够在各种语言之间精确排序
# utf8mb4_general_ci: 没有实现 Unicode 排序规则，在遇到某些特殊语言或者字符集，排序结果可能不一致。
#
# utf8=utf8mb3
# utf8_bin：二进制存储字符串，区分大小写
# utf8_general_cs：大小写敏感
# utf8_general_ci：大小写不敏感


USE `mall`;

SET NAMES utf8mb4;
# SET NAMES utf8;
# equal to
# SET character_set_client='utf8';
# SET character_set_connection='utf8';
# SET character_set_results='utf8';

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`          INT(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '用户',
    `username`    VARCHAR(50) NOT NULL
        COMMENT '用户名',
    `password`    VARCHAR(50) NOT NULL
        COMMENT '密码(MD5盐值加密)',
    `email`       VARCHAR(50) COMMENT '电子邮箱',
    `mobile`      VARCHAR(20) COMMENT '手机号码',
    `question`    VARCHAR(100) COMMENT '密保问题',
    `answer`      VARCHAR(100) COMMENT '密保答案',
    `role`        TINYINT     NOT NULL DEFAULT 1
        COMMENT '用户角色(0-管理员 / 1-消费者)',
    `create_time` DATETIME    NOT NULL DEFAULT NOW(),
    `update_time` DATETIME    NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    UNIQUE KEY `user.uk.username` (`username`) USING BTREE,
    UNIQUE KEY `user.uk.email` (`email`) USING BTREE,
    UNIQUE KEY `user.uk.mobile` (`mobile`) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

INSERT INTO user(id, username, password, email, mobile, question, answer, role)
VALUES (999999, 'admin', '0F9C57820F575425E7A75A60B118E747', 'admin@gmail.com', '', '', '',
        0);
-- ----------------------------
--  Table structure for `shipping`
-- ----------------------------
DROP TABLE IF EXISTS `shipping`;
CREATE TABLE `shipping`
(
    `id`          INT(11)      NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '收货地址',
    `user_id`     INT(11)      NOT NULL
        COMMENT '所属用户的 ID',
    `name`        VARCHAR(20)  NOT NULL
        COMMENT '收货人姓名',
    `mobile`      VARCHAR(20)  NOT NULL
        COMMENT '移动电话',
    `province`    VARCHAR(20)  NOT NULL
        COMMENT '省份',
    `city`        VARCHAR(20)  NOT NULL
        COMMENT '城市',
    `district`    VARCHAR(20)  NOT NULL
        COMMENT '区/县',
    `address`     VARCHAR(200) NOT NULL
        COMMENT '详细地址',
    `zip`         VARCHAR(6)
        COMMENT '邮编',
    `create_time` DATETIME     NOT NULL DEFAULT NOW(),
    `update_time` DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    KEY `shipping.key.user_id` (`user_id`) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

-- ----------------------------
--  Table structure for `category`
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
    `id`          INT(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '商品类别',
    `parent_id`   INT(11)     NOT NULL DEFAULT 0
        COMMENT '父级类别的 ID(0-顶级类别)',
    `name`        VARCHAR(50) NOT NULL
        COMMENT '商品类别名称',
    `status`      TINYINT     NOT NULL DEFAULT 1
        COMMENT '商品类别状态(0-禁用 / 1-启用)',
    `create_time` DATETIME    NOT NULL DEFAULT NOW(),
    `update_time` DATETIME    NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    KEY `category.key.parent_id` (`parent_id`) USING BTREE,
    UNIQUE KEY `category.uk.parent_id-name` (`parent_id`, `name`) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

-- ----------------------------
--  Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`
(
    `id`          INT(11)        NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '商品',
    `category_id` INT(11)        NOT NULL
        COMMENT '商品类别的 ID',
    `name`        VARCHAR(100)   NOT NULL
        COMMENT '商品名称',
    `subtitle`    VARCHAR(200)
        COMMENT '商品副标题',
    `main_image`  VARCHAR(500)
        COMMENT '商品主图(图片 URL 相对地址)',
    `sub_images`  TEXT COMMENT '商品组图(使用逗号分隔的多个图片 URL 相对地址)',
    `detail`      TEXT COMMENT '商品详情',
    `price`       DECIMAL(20, 2) NOT NULL
        COMMENT '商品价格(元，保留两位小数)',
    `stock`       INT(11)        NOT NULL DEFAULT 0
        COMMENT '库存数量',
    `status`      TINYINT        NOT NULL DEFAULT 1
        COMMENT '销售状态(0-已下架 / 1-出售中)',
    `create_time` DATETIME       NOT NULL DEFAULT NOW(),
    `update_time` DATETIME       NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    KEY `product.key.category_id` (`category_id`) USING BTREE,
    KEY `product.key.name` (`name`) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

-- ----------------------------
--  Table structure for `cart_item`
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`
(
    `id`          INT(11)  NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '购物车商品条目',
    `user_id`     INT(11)  NOT NULL
        COMMENT '所属用户的 ID',
    `product_id`  INT(11)  NOT NULL
        COMMENT '所属商品的 ID',
    `quantity`    INT(11)  NOT NULL
        COMMENT '数量',
    `checked`     BOOLEAN  NOT NULL DEFAULT FALSE
        COMMENT '勾选状态(true-已勾选 / false-未勾选)',
    `create_time` DATETIME NOT NULL DEFAULT NOW(),
    `update_time` DATETIME NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    KEY `cart_item.key.user_id` (`user_id`) USING BTREE,
    UNIQUE KEY `cart_item.uk.user_id-product_id` (`user_id`, `product_id`)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

-- ----------------------------
--  Table structure for `order`
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`
(
    `id`           INT(11)        NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '订单',
    `order_no`     BIGINT(20)     NOT NULL
        COMMENT '订单号',
    `user_id`      INT(11)        NOT NULL
        COMMENT '所属用户的 ID',
    `shipping_id`  INT(11)        NOT NULL
        COMMENT '收货地址的 ID',
    `status`       TINYINT        NOT NULL DEFAULT 10
        COMMENT '订单状态(0-已取消 / 10-待付款 / 30-待发货 / 50-待签收 / 70-交易完成 / 90-交易关闭)',
    `postage`      DECIMAL(20, 2) NOT NULL DEFAULT 0
        COMMENT '运费',
    `payment`      DECIMAL(20, 2) NOT NULL
        COMMENT '支付金额',
    `payment_type` TINYINT        NOT NULL DEFAULT 1
        COMMENT '支付类型(1-在线支付 / 2-货到付款)',
    `payment_time` DATETIME
        COMMENT '支付时间(支付平台支付成功后回调的支付时间)',
    `send_time`    DATETIME
        COMMENT '发货时间',
    `end_time`     DATETIME
        COMMENT '交易完成时间',
    `close_time`   DATETIME
        COMMENT '交易关闭时间',
    `create_time`  DATETIME       NOT NULL DEFAULT NOW(),
    `update_time`  DATETIME       NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    KEY `order.key.user_id` (`user_id`) USING BTREE,
    UNIQUE KEY `order.uk.order_no` (`order_no`) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

-- ----------------------------
--  Table structure for `order_item`
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`
(
    `id`            INT(11)        NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '订单商品明细',
    `order_no`      BIGINT(20)     NOT NULL
        COMMENT '订单号',
    `product_id`    INT(11)        NOT NULL
        COMMENT '商品的 ID',
    `product_name`  VARCHAR(100)   NOT NULL
        COMMENT '商品名称(快照：生成订单时商品的名称，不与商品名称产生关联)',
    `product_image` VARCHAR(500)
        COMMENT '商品主图(快照：生成订单时商品的主图 URL，不与商品主图 URL 产生关联)',
    `quantity`      INT(11)        NOT NULL
        COMMENT '商品数量',
    `unit_price`    DECIMAL(20, 2) NOT NULL
        COMMENT '商品单价(快照：生成订单时商品的单价，不与商品价格产生关联)',
    `total_price`   DECIMAL(20, 2) NOT NULL
        COMMENT '商品总价',
    `create_time`   DATETIME       NOT NULL DEFAULT NOW(),
    `update_time`   DATETIME       NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    KEY `order_item.key.order_no` (`order_no`) USING BTREE,
    UNIQUE KEY `order_item.uk.order_no-product_id` (`order_no`, `product_id`) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

-- ----------------------------
--  Table structure for `payment_info`
-- ----------------------------
DROP TABLE IF EXISTS `payment_info`;
CREATE TABLE `payment_info`
(
    `id`              INT(11)      NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '订单支付信息',
    `user_id`         INT(11)      NOT NULL
        COMMENT '所属用户的 ID',
    `order_no`        BIGINT(20)   NOT NULL
        COMMENT '所属订单的 订单号',
    `platform`        TINYINT      NOT NULL
        COMMENT '支付平台(1-支付宝 / 2-微信)',
    `platform_number` VARCHAR(200) NOT NULL
        COMMENT '支付流水号',
    `platform_status` VARCHAR(20)  NOT NULL
        COMMENT '支付状态',
    `create_time`     DATETIME     NOT NULL DEFAULT NOW(),
    `update_time`     DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    UNIQUE KEY `payment_info.key.order_no` (`order_no`) USING BTREE,
    UNIQUE KEY `payment_info.uk.platform_number` (`platform_number`) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1000000
    DEFAULT CHARSET = utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;