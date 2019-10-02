package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ProductMapper {
    String selectAll = "select id, category_id, name, subtitle, main_image, sub_images, detail, price, stock, status, create_time, update_time from product ";

    @Select(selectAll)
    List<Product> selectAll();

    @Select(selectAll + "where id = #{id}")
    Product selectByPrimaryKey(int id);

    @Delete("delete from product where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into product <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Product product);

    @Update("update product <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(Product product);

    @Update("update product <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(Product product);
}