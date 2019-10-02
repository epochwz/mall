package fun.epoch.mall.dao;

public interface ShippingMapper {
    String selectAll = "select id, user_id, name, mobile, province, city, district, address, zip, create_time, update_time from shipping ";
}