package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.vo.OrderVo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.manage.order.detail;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockJsons.EXPECTED_JSON_OF_ORDER_DETAIL;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.OrderKeys.orderNo;
import static fun.epoch.mall.mvc.common.Keys.Tables.*;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ManageOrderTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, MANAGER)
                .database(COMMON_SQLS)
                .launchTable(order, order_item, shipping, product);
    }

    /**
     * 查看订单详情
     * <p>
     * 200  查看成功，返回订单详情
     * 404  找不到该订单
     */
    @Test
    public void testDetail_200_withOrderDetail() {
        ResultActions result = perform(SUCCESS, post(detail).param("orderNo", orderNo));

        OrderVo actual = orderVoFromAndClean(result, false);
        OrderVo expected = orderVoFrom(EXPECTED_JSON_OF_ORDER_DETAIL);
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testDetail_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(detail).param("orderNo", idNotExist));
    }
}
