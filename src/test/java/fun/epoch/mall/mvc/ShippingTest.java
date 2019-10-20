package fun.epoch.mall.mvc;

import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.mvc.common.Apis.portal.shipping.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.IdNotExist;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.AUTO_INCREMENT;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.ShippingKeys.*;
import static fun.epoch.mall.mvc.common.Keys.Tables.shipping;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId2;
import static fun.epoch.mall.utils.response.ResponseCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ShippingTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, CONSUMER)
                .database(COMMON_SQLS)
                .launchTable(shipping);
    }

    /**
     * 查看收货地址
     * <p>
     * 200  查看成功，返回收货地址信息
     * 404  收货地址不存在
     * 403  收货地址不属于当前用户
     */
    @Test
    public void testDetail_200_withShipping() {
        perform(post(detail).param("id", shippingId), SUCCESS, userId, shippingId, shippingName);
    }

    @Test
    public void testDetail_404_whenShippingNotExist() {
        perform(NOT_FOUND, post(detail).param("id", idNotExist));
    }

    @Test
    public void testDetail_403_whenShippingNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).perform(FORBIDDEN, post(detail).param("id", shippingId));
    }

    /**
     * 查看收货地址列表
     * <p>
     * 200  查看成功，返回收货地址列表
     */
    @Test
    public void testList_200_withShippingList() {
        perform(post(list), SUCCESS, userId, shippingId, shippingName, shippingId2, shippingName2, "\"size\":2");
    }

    /**
     * 添加收货地址
     * <p>
     * 200  添加成功，返回新增收货地址的 id
     */
    @Test
    public void testAdd_200_withShippingId() {
        this.database().reset(AUTO_INCREMENT, shipping).launch();

        postJson(add, mock().build(), SUCCESS, shippingId);
    }

    /**
     * 删除收货地址
     * <p>
     * 200  删除成功
     * 404  收货地址不存在
     * 403  收货地址不属于当前用户
     */
    @Test
    public void testDelete_200() {
        perform(SUCCESS, post(detail).param("id", shippingId));
        perform(SUCCESS, post(delete).param("id", shippingId));
        perform(NOT_FOUND, post(detail).param("id", shippingId));
    }

    @Test
    public void testDelete_404_whenShippingNotExist() {
        perform(NOT_FOUND, post(delete).param("id", idNotExist));
    }

    @Test
    public void testDelete_403_whenShippingNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).perform(FORBIDDEN, post(delete).param("id", shippingId));
    }

    /**
     * 修改收货地址
     * <p>
     * 200  修改成功，返回修改后的收货地址
     * 404  收货地址不存在
     * 403  收货地址不属于当前用户
     */
    @Test
    public void testUpdate_200_withShipping() {
        postJson(update, mock().id(Integer.valueOf(shippingId)).name(shippingName2).build(), SUCCESS, userId, shippingId, shippingName2);
    }

    @Test
    public void testUpdate_404_whenShippingNotExist() {
        postJson(update, mock().id(IdNotExist).build(), NOT_FOUND);
    }

    @Test
    public void testUpdate_403_whenShippingNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).postJson(update, mock().id(Integer.valueOf(shippingId)).name(shippingName2).build(), FORBIDDEN);
    }

    private Shipping.ShippingBuilder mock() {
        return Shipping.builder().name("小明").mobile("15623336666").province("广东省").city("广州市").district("小谷围街道").address("宇宙工业大学");
    }
}
