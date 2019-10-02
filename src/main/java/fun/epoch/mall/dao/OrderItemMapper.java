package fun.epoch.mall.dao;

public interface OrderItemMapper {
    String selectAll = "select id, order_no, product_id, product_name, product_image, quantity, unit_price, total_price, create_time, update_time from order_item ";
}