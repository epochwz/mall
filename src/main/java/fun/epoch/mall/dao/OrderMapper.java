package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderMapper {
    String selectAll = "select id, order_no, user_id, shipping_id, status, postage, payment, payment_type, payment_time, send_time, end_time, close_time, create_time, update_time from `order` ";

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

    @Select(selectAll + "where order_no=#{orderNo}")
    Order selectByOrderNo(long orderNo);

    @Select({"<script>" +
            selectAll +
            "<where>" +
            "<if test=\"orderNo != null \"> AND order_no like CONCAT('%',#{orderNo},'%')</if>" +
            "<if test=\"userId != null \"> AND user_id=#{userId}</if>" +
            "<if test=\"status != null \"> AND status=#{status}</if>" +
            "<if test=\"startTime != null \"> AND unix_timestamp(create_time) >= #{startTime}/1000</if>" +
            "<if test=\"endTime != null \"> AND unix_timestamp(create_time) &lt;= #{endTime}/1000</if>" +
            "<if test=\"keyword != null \"> AND order_no in (select order_no from order_item where product_name like CONCAT('%',#{keyword},'%'))</if>" +
            "</where>" +
            "</script>"})
    List<Order> search(@Param("orderNo") Long orderNo, @Param("userId") Integer userId, @Param("keyword") String keyword, @Param("status") Integer status, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}