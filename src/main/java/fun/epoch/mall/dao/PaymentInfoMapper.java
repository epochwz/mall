package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.PaymentInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PaymentInfoMapper {
    String selectAll = "select id, user_id, order_no, platform, platform_number, platform_status, create_time, update_time from payment_info ";

    @Select(selectAll)
    List<PaymentInfo> selectAll();

    @Select(selectAll + "where id = #{id}")
    PaymentInfo selectByPrimaryKey(int id);

    @Delete("delete from payment_info where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into payment_info <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(PaymentInfo paymentInfo);

    @Update("update payment_info <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(PaymentInfo paymentInfo);

    @Update("update payment_info <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(PaymentInfo paymentInfo);
}