# allChecked
INSERT INTO cart_item(id, user_id, product_id, quantity, checked)
VALUES (1, 1000000, 1, 3, true),
       (2, 1000000, 2, 5, true),
       (3, 1000000, 3, 2, true),
       (4, 1000000, 4, 1, true);

# productQuantityLimited
INSERT INTO cart_item(id, user_id, product_id, quantity, checked)
VALUES (1, 1000000, 1, 3, true),
       (2, 1000000, 2, 10, true),
       (3, 1000000, 3, 10, true),
       (4, 1000000, 4, 1, false);

# productNotExist
INSERT INTO cart_item(id, user_id, product_id, quantity, checked)
VALUES (5, 1000000, 5, 3, true),
       (6, 1000000, 6, 3, true);

# addProductNotInCartBefore
INSERT INTO cart_item(id, user_id, product_id, quantity, checked)
VALUES (1, 1000000, 1, 3, true),
       (2, 1000000, 2, 5, true),
       (4, 1000000, 4, 1, false);