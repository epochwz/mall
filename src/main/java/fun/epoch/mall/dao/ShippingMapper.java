package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.Shipping;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ShippingMapper {
    String selectAll = "select id, user_id, name, mobile, province, city, district, address, zip, create_time, update_time from shipping ";

    @Select(selectAll)
    List<Shipping> selectAll();

    @Select(selectAll + "where id = #{id}")
    Shipping selectByPrimaryKey(int id);

    @Delete("delete from shipping where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into shipping <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Shipping shipping);

    @Update("update shipping <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(Shipping shipping);

    @Update("update shipping <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(Shipping shipping);

    @Select(selectAll + "where user_id=#{userId}")
    List<Shipping> selectByUserId(int userId);
}