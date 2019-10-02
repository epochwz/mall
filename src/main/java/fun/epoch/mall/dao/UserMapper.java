package fun.epoch.mall.dao;

public interface UserMapper {
    String selectAll = "select id, username, password, email, mobile, question, answer, role, create_time, update_time from user ";
}