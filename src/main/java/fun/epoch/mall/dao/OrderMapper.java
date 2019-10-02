package fun.epoch.mall.dao;

public interface OrderMapper {
    String selectAll = "select id, order_no, user_id, shipping_id, status, postage, payment, payment_type, payment_time, send_time, end_time, close_time, create_time, update_time from order ";
}