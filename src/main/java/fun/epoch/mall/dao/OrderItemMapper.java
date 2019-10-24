package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderItemMapper {
    String selectAll = "select id, order_no, product_id, product_name, product_image, quantity, unit_price, total_price, create_time, update_time from order_item ";

    @Select(selectAll)
    List<OrderItem> selectAll();

    @Select(selectAll + "where id = #{id}")
    OrderItem selectByPrimaryKey(int id);

    @Delete("delete from order_item where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into order_item <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(OrderItem orderProductItem);

    @Update("update order_item <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(OrderItem orderProductItem);

    @Update("update order_item <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(OrderItem orderProductItem);

    @Select(selectAll + "where order_no=#{orderNo}")
    List<OrderItem> selectByOrderNo(long orderNo);
}