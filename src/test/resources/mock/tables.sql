# 0F9C57820F575425E7A75A60B118E747  admin_pass
# 17C028EADDA61E3E2F05787FDA6B7965  epochwz_pass
# 6A1E8EFFF6B56F9CDB0FCD3EF6B8DB2A  epoch_pass
# user
INSERT INTO user(id, username, password, email, mobile, question, answer, role)
VALUES (1000000, 'epoch', '6A1E8EFFF6B56F9CDB0FCD3EF6B8DB2A', 'epoch@gmail.com', '15622223333', 'epoch\'s question', 'epoch\'s answer', 1),
       (1000001, 'epochwz', '17C028EADDA61E3E2F05787FDA6B7965', 'epochwz@gmail.com', '15611112222', '', '', 1),
       (1000002, 'admin', '0F9C57820F575425E7A75A60B118E747', 'admin@gmail.com', '', '', '', 0);

# shipping
INSERT INTO shipping(id, user_id, name, mobile, province, city, district, address, zip)
VALUES (1, 1000000, 'epoch', '15623333333', '广东省', '广州市', '小谷围街道', '广东工业大学', '516000'),
       (2, 1000000, 'epochwz', '15623333333', '广东省', '广州市', '小谷围街道', '广东工业大学', '516000');

# category
INSERT INTO category(id, parent_id, name)
VALUES (1, 0, '图书'),
       (11, 1, '小说'),
       (111, 11, '玄幻'),
       (112, 11, '言情'),
       (12, 1, '文学'),
       (2, 0, '服装'),
       (21, 2, '衬衫'),
       (22, 2, '裙子'),
       (221, 22, '连衣裙'),
       (222, 22, '百褶裙');

# product
INSERT
INTO product(id, category_id, name, subtitle, main_image, sub_images, detail, price, stock, status)
VALUES (1, 111, '斗破苍穹', '天蚕土豆', 'doupo.jpg', null, '还不错的小说', 13.4, 99, 1),
       (2, 111, '武动乾坤', '天蚕土豆', '', 'wudong.jpg,wudong2.jpg', '一般般的小说', 11.2, 5, 1),
       (3, 111, '大主宰', '天蚕土豆', '', '', '不咋样的小说', 8.3, 5, 1),
       (4, 111, '悲伤逆流成河', '', '', '', '', 5.1, 100, 1),
       (5, 221, '悲伤', '', 'beishang.jpg', 'bei.jpg', '', 5.1, 100, 0);

# cart_item
INSERT INTO cart_item(id, user_id, product_id, quantity, checked)
VALUES (1, 1000000, 1, 3, true),
       (2, 1000000, 2, 5, true),
       (3, 1000000, 3, 2, true),
       (4, 1000000, 4, 1, false);

# order_item
INSERT INTO order_item (id, order_no, product_id, product_name, product_image, unit_price, quantity, total_price)
VALUES (1, 1565625618510, 1, '斗破苍穹', 'doupo.jpg', 13.4, 2, 26.8),
       (2, 1565625618510, 2, '武动乾坤', 'wudong.jpg', 11.2, 2, 22.4),
       (3, 1565625618510, 3, '百褶裙', 'baizhequn.jpg', 11.2, 3, 33.6);

# `order`
INSERT INTO `order` (order_no, user_id, shipping_id, payment, status, payment_time)
VALUES (1565625618510, 1000000, 1, 82.8, 30, '2019-09-09 09:09:09');

# `order`
INSERT INTO `order` (order_no, user_id, shipping_id, payment, status)
VALUES (9565625618500, 1000000, 0, 0, 0),
       (9565625618511, 1000000, 0, 0, 10),
       (9565625618533, 1000000, 0, 0, 30),
       (9565625618555, 1000000, 0, 0, 50),
       (9565625618577, 1000000, 0, 0, 70),
       (9565625618599, 1000000, 0, 0, 90),
       (9565625618522, 1000001, 0, 0, 30),
       (9565625618544, 1000001, 0, 0, 10);

# `order`
INSERT INTO `order` (order_no, user_id, shipping_id, payment, create_time)
VALUES (8565625618666, 1000001, 0, 0, '2019-09-15 09:26:35'),
       (8565625618777, 1000000, 0, 0, '2019-09-17 09:26:35'),
       (8565625618888, 1000000, 0, 0, '2019-09-18 09:26:35'),
       (8565625618999, 1000001, 0, 0, '2019-09-20 09:26:35');