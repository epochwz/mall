package fun.epoch.mall.dao;

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

    List<CartItem> selectByUserId(int userId);

    int selectCountByUserId(int userId);

    CartItem selectByUserIdAndProductId(int userId, int productId);

    int deleteByUserIdAndProductIds(int userId, List<Integer> productIds);

    int updateQuantityByUserIdAndProductId(int userId, int productId, int count);

    int updateCheckStatusByUserIdAndProductId(int userId, int productId, boolean checked);

    int updateCheckStatusByUserId(int userId, boolean checked);
}