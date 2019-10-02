package fun.epoch.mall.dao;

public interface CartItemMapper {
    String selectAll = "select id, user_id, product_id, quantity, checked, create_time, update_time from cart_item ";
}