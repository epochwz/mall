package fun.epoch.mall.dao;

import fun.epoch.mall.dao.langdriver.ForeachLangDriver;
import fun.epoch.mall.dao.langdriver.InsertAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateAllLangDriver;
import fun.epoch.mall.dao.langdriver.UpdateSelectiveLangDriver;
import fun.epoch.mall.entity.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CartItemMapper {
    String selectAll = "select id, user_id, product_id, quantity, checked, create_time, update_time from cart_item ";

    @Select(selectAll)
    List<CartItem> selectAll();

    @Select(selectAll + "where id = #{id}")
    CartItem selectByPrimaryKey(int id);

    @Delete("delete from cart_item where id = #{id}")
    int deleteByPrimaryKey(int id);

    @Insert("insert into cart_item <all>")
    @Lang(InsertAllLangDriver.class)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(CartItem cartItem);

    @Update("update cart_item <all>")
    @Lang(UpdateAllLangDriver.class)
    int updateByPrimaryKey(CartItem cartItem);

    @Update("update cart_item <selective>")
    @Lang(UpdateSelectiveLangDriver.class)
    int updateSelectiveByPrimaryKey(CartItem cartItem);

    @Select(selectAll + "where user_id=#{userId}")
    List<CartItem> selectByUserId(int userId);

    @Select("select IFNULL(sum(quantity), 0) from cart_item where user_id=#{userId}")
    int selectCountByUserId(int userId);

    @Select(selectAll + "where user_id=#{userId} and product_id=#{productId}")
    CartItem selectByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId);

    @Delete("delete from cart_item where user_id=#{userId} and product_id in <list>")
    @Lang(ForeachLangDriver.class)
    int deleteByUserIdAndProductIds(@Param("userId") int userId, @Param("list") List<Integer> productIds);

    @Update("update cart_item set quantity=#{quantity} where user_id=#{userId} and product_id=#{productId}")
    int updateQuantityByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId, @Param("quantity") int quantity);

    @Update("update cart_item set checked=#{checked} where user_id=#{userId} and product_id=#{productId}")
    int updateCheckStatusByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId, @Param("checked") boolean checked);

    @Update("update cart_item set checked=#{checked} where user_id=#{userId}")
    int updateCheckStatusByUserId(@Param("userId") int userId, @Param("checked") boolean checked);

    @Select(selectAll + "where user_id=#{userId} and checked=true")
    List<CartItem> selectCheckedItemsByUserId(int userId);

    @Delete("delete from cart_item where user_id=#{userId} and checked=true")
    int deleteCheckedByUserId(int userId);
}