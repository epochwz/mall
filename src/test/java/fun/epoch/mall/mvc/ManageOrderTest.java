package fun.epoch.mall.mvc;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.common.Constant;
import fun.epoch.mall.mvc.common.Apis;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.mvc.common.Keys;
import fun.epoch.mall.vo.OrderVo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.Constant.OrderStatus.*;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.manage.order.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockJsons.EXPECTED_JSON_OF_ORDER_DETAIL;
import static fun.epoch.mall.mvc.common.Keys.MockJsons.EXPECTED_JSON_OF_ORDER_SEARCH;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.ORDER_SQLS;
import static fun.epoch.mall.mvc.common.Keys.OrderKeys.*;
import static fun.epoch.mall.mvc.common.Keys.ProductKeys.*;
import static fun.epoch.mall.mvc.common.Keys.Tables.*;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.DateTimeUtils.timeStrFrom;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static fun.epoch.mall.utils.response.ResponseCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ManageOrderTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, MANAGER)
                .database(COMMON_SQLS, ORDER_SQLS)
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
        ResultActions result = perform(SUCCESS, post(Apis.manage.order.detail).param("orderNo", orderNo));

        OrderVo actual = orderVoFromAndClean(result, false);
        OrderVo expected = orderVoFrom(EXPECTED_JSON_OF_ORDER_DETAIL);
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testDetail_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(Apis.manage.order.detail).param("orderNo", idNotExist));
    }

    /**
     * 搜索订单
     * 200  搜索成功，返回订单列表
     * 200  搜索成功 (动态筛选)
     */
    @Test
    public void testSearch_200_withPageInfo() {
        PageInfo<OrderVo> actual = orderVoPageInfoFromAndClean(perform(SUCCESS, post(search)));
        PageInfo<OrderVo> expected = orderVoPageInfoFrom(EXPECTED_JSON_OF_ORDER_SEARCH);

        assertObjectEquals(expected, actual);
    }

    @Test
    public void testSearch_selective() {
        assertSearchedSize(13, post(search));
        assertSearchedSize(0, post(search).param("orderNo", idNotExist));
        assertSearchedSize(1, post(search).param("orderNo", orderNo));
        assertSearchedSize(8, post(search).param("orderNo", searchPrefix));
        assertSearchedSize(9, post(search).param("userId", userId));
        assertSearchedSize(3, post(search).param("status", String.valueOf(PAID.getCode())));
        assertSearchedSize(1, post(search)
                .param("orderNo", searchPrefix)
                .param("userId", userId)
                .param("status", String.valueOf(PAID.getCode()))
        );
        assertSearchedSize(3, post(search)
                .param("orderNo", searchPrefixForTime)
                .param("startTime", timeStrFrom("2019-09-17 09:26:35"))
        );
        assertSearchedSize(3, post(search)
                .param("orderNo", searchPrefixForTime)
                .param("endTime", timeStrFrom("2019-09-18 09:26:35"))
        );
        assertSearchedSize(2, post(search)
                .param("orderNo", searchPrefixForTime)
                .param("startTime", timeStrFrom("2019-09-18 09:26:35"))
                .param("endTime", timeStrFrom("2019-09-20 09:26:35"))
        );
        assertSearchedSize(1, post(search)
                .param("orderNo", searchPrefixForTime)
                .param("startTime", timeStrFrom("2019-09-18 09:26:35"))
                .param("endTime", timeStrFrom("2019-09-20 09:26:35"))
                .param("userId", userId)
        );
    }

    /**
     * 订单发货
     * 404  找不到该订单
     * 400  发货失败：已取消 / 未付款 / 已完成 / 已关闭
     * 200  发货成功 (可发货的订单状态：已付款 / 已发货)
     * 200  发货成功，更新订单状态
     */
    @Test
    public void testShip_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(ship).param("orderNo", idNotExist));
    }

    @Test
    public void testShip_400_whenOrderStatusNotCorrect() {
        perform(ERROR, post(ship).param("orderNo", orderCanceled));
        perform(ERROR, post(ship).param("orderNo", orderUnPaid));
        perform(ERROR, post(ship).param("orderNo", orderFinished));
        perform(ERROR, post(ship).param("orderNo", orderClosed));
    }

    @Test
    public void testShip_200_whenOrderStatusCorrect() {
        perform(SUCCESS, post(ship).param("orderNo", orderPaid));
        perform(SUCCESS, post(ship).param("orderNo", orderShipped));
    }

    @Test
    public void testShip_200_whileOrderStatusUpdated() {
        perform(SUCCESS, post(ship).param("orderNo", orderPaid));
        assertOrderStatus(SHIPPED, orderPaid);
    }

    /**
     * 关闭订单
     * 404  找不到该订单
     * 400  关闭失败：已取消 / 已付款 / 已发货 / 已完成
     * 200  关闭成功 (可关闭的订单状态：未付款 / 已关闭)
     * 200  关闭成功，更新订单状态
     * 200  关闭成功，恢复商品库存
     */
    @Test
    public void testClose_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(close).param("orderNo", idNotExist));
    }

    @Test
    public void testClose_400_whenOrderStatusNotCorrect() {
        perform(ERROR, post(close).param("orderNo", orderCanceled));
        perform(ERROR, post(close).param("orderNo", orderPaid));
        perform(ERROR, post(close).param("orderNo", orderShipped));
        perform(ERROR, post(close).param("orderNo", orderFinished));
    }

    @Test
    public void testClose_200_whenOrderStatusCorrect() {
        perform(SUCCESS, post(close).param("orderNo", orderUnPaid));
        perform(SUCCESS, post(close).param("orderNo", orderClosed));
    }

    @Test
    public void testClose_200_whileOrderStatusUpdated() {
        perform(SUCCESS, post(close).param("orderNo", orderUnPaid));
        assertOrderStatus(CLOSED, orderUnPaid);
    }

    @Test
    public void testClose_200_whileProductStockRestored() {
        this.database().launchCase(Keys.MockCases.CASE_ORDER_CLOSE_ORDER);
        perform(SUCCESS, post(close).param("orderNo", orderNo));
        assertProductStock(101, productId);
        assertProductStock(7, productId2);
        assertProductStock(8, productId3);
    }

    private void assertOrderStatus(Constant.OrderStatus expectedStatus, String orderNo) {
        assertOrderStatus(expectedStatus, Apis.manage.order.detail, orderNo);
    }

    private void assertProductStock(int expectedStock, String productId) {
        assertProductStock(expectedStock, Apis.manage.product.detail, productId);
    }
}
