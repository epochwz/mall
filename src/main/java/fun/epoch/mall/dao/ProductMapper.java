package fun.epoch.mall.dao;

public interface ProductMapper {
    String selectAll = "select id, category_id, name, subtitle, main_image, sub_images, detail, price, stock, status, create_time, update_time from product ";
}