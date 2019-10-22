package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderMapper {
    String selectAll = "select id, order_no, user_id, shipping_id, status, postage, payment, payment_type, payment_time, send_time, end_time, close_time, create_time, update_time from order ";

    @Select(selectAll)
    List<Order> selectAll();

    @Select(selectAll + "where id = #{id}")
    Order selectByPrimaryKey(int id);

    @Delete("delete from `order` where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into `order` <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Order order);

    @Update("update `order` <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(Order order);

    @Update("update `order` <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(Order order);

    Order selectByOrderNo(long orderNo);

    List<Order> search(Long orderNo, Integer userId, String keyword, Integer status, Long startTime, Long endTime);
}