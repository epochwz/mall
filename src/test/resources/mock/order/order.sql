# closeOrder
UPDATE `order`
SET `status`=10
where order_no = '1565625618510';

# search
INSERT INTO order_item (id, order_no, product_id, product_name, product_image, unit_price, quantity, total_price)
VALUES (1, 1565625618510, 1, '斗破苍穹', 'doupo.jpg', 13.4, 2, 26.8),
       (2, 1565625618510, 2, '武动乾坤', 'wudong.jpg', 11.2, 2, 22.4),
       (3, 1565625618510, 3, '百褶裙', 'baizhequn.jpg', 11.2, 3, 33.6),
       (4, 1565625618510, 4, '超短裙', 'doupo.jpg', 13.4, 2, 26.8),
       (5, 9565625618511, 5, '连衣裙', 'wudong.jpg', 11.2, 2, 22.4),
       (6, 9565625618544, 3, '百褶裙', 'baizhequn.jpg', 11.2, 3, 33.6);

# search
INSERT INTO `order` (order_no, user_id, shipping_id, payment, status)
VALUES (1565625618510, 1000000, 0, 0, 10),
       (9565625618511, 1000000, 0, 0, 10),
       (9565625618544, 1000001, 0, 0, 10);