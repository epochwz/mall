package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.mvc.common.Apis.portal.shipping.detail;
import static fun.epoch.mall.mvc.common.Apis.portal.shipping.list;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.ShippingKeys.*;
import static fun.epoch.mall.mvc.common.Keys.ShippingKeys.shippingName2;
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
}
