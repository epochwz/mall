package fun.epoch.mall.dao;

public interface PaymentInfoMapper {
    String selectAll = "select id, user_id, order_no, platform, platform_number, platform_status, create_time, update_time from payment_info ";
}