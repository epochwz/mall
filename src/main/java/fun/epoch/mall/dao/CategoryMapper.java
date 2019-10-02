package fun.epoch.mall.dao;

public interface CategoryMapper {
    String selectAll = "select id, parent_id, name, status, create_time, update_time from category ";
}