package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.Apis;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.vo.OrderVo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.OrderStatus.CANCELED;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.portal.order.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockCases.*;
import static fun.epoch.mall.mvc.common.Keys.MockJsons.*;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.ORDER_SQLS;
import static fun.epoch.mall.mvc.common.Keys.OrderKeys.*;
import static fun.epoch.mall.mvc.common.Keys.ProductKeys.*;
import static fun.epoch.mall.mvc.common.Keys.ShippingKeys.shippingId;
import static fun.epoch.mall.mvc.common.Keys.Tables.*;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId2;
import static fun.epoch.mall.utils.response.ResponseCode.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class OrderTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init().session(userId, CONSUMER)
                .database(COMMON_SQLS, ORDER_SQLS)
                .launchTable(order, order_item, cart_item, product, shipping);
    }

    /**
     * 查看订单详情
     * <p>
     * 404  找不到该订单
     * 403  无权限，该订单不属于当前用户
     * 200  查看成功，返回订单详情
     */
    @Test
    public void testDetail_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(detail).param("orderNo", idNotExist));
    }

    @Test
    public void testDetail_403_whenOrderNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).perform(FORBIDDEN, post(detail).param("orderNo", orderNo));
    }

    @Test
    public void testDetail_200_withOrderDetail() {
        ResultActions result = perform(SUCCESS, post(detail).param("orderNo", orderNo));

        OrderVo actual = orderVoFromAndClean(result, false);
        OrderVo expected = orderVoFrom(EXPECTED_JSON_OF_ORDER_DETAIL);
        assertObjectEquals(expected, actual);
    }

    /**
     * 搜索订单
     * <p>
     * 200  搜索成功 (商品关键字筛选)
     */
    @Test
    public void testSearch_selective() {
        this.database().launchCase(CASE_SEARCH_ORDER_BY_KEYWORD, order, order_item);

        assertSearchedSize(2, post(search).param("keyword", "裙"));
        assertSearchedSize(1, post(search)
                .param("keyword", "裙")
                .param("orderNo", orderNo)
        );
    }

    /**
     * 预览订单
     * <p>
     * 400  购物车中没有选中的商品
     * 400  某商品不存在
     * 400  某商品已下架
     * 400  某商品库存不足
     * 200  预览成功，返回订单预览信息
     */
    @Test
    public void testPreview_400_whenCartHasNoProduct() {
        this.database().truncate(cart_item).launch();
        perform(post(preview).param("shippingId", shippingId), ERROR, "购物车中没有选中的商品");
    }

    @Test
    public void testPreview_400_whenCartHasNoCheckedProduct() {
        this.database().launchCase(CASE_CART_HAS_NO_CHECKED_PRODUCT, cart_item);
        perform(post(preview).param("shippingId", shippingId), ERROR, "购物车中没有选中的商品");
    }

    @Test
    public void testPreview_400_whenProductNotExist() {
        this.database().launchCase(CASE_PRODUCT_NOT_EXIST, cart_item, product);
        perform(post(preview).param("shippingId", shippingId), NOT_FOUND, "不存在");
    }

    @Test
    public void testPreview_400_whenProductOffSale() {
        this.database().launchCase(CASE_PRODUCT_IS_OFF_SALE, cart_item, product);
        perform(post(preview).param("shippingId", shippingId), NOT_FOUND, "已下架");
    }

    @Test
    public void testPreview_400_whenProductStockNotEnough() {
        this.database().launchCase(CASE_PRODUCT_STOCK_NOT_ENOUGH, cart_item, product);
        perform(post(preview).param("shippingId", shippingId), ERROR, "库存不足");
    }

    @Test
    public void testPreview_200_andReturnOrderPreview() {
        ResultActions resultActions = perform(SUCCESS, post(preview));

        OrderVo actual = orderVoFrom(resultActions);
        OrderVo expected = orderVoFrom(EXPECTED_JSON_OF_ORDER_PREVIEW);

        assertObjectEquals(expected, actual);
    }

    /**
     * 创建订单
     * 404  收货地址不存在 (该收货地址不属于当前用户)
     * 400  购物车中没有选中的商品
     * 400  某商品不存在
     * 400  某商品已下架
     * 400  某商品库存不足
     * 200  创建成功，返回订单详细信息
     * 200  创建成功，清除购物车
     * 200  创建成功，减少商品库存
     */
    @Test
    public void testCreate_404_whenShippingNotExist() {
        perform(NOT_FOUND, post(create).param("shippingId", idNotExist));
    }

    @Test
    public void testCreate_404_whenShippingNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).perform(FORBIDDEN, post(create).param("shippingId", shippingId));
    }

    @Test
    public void testCreate_400_whenCartHasNoProduct() {
        this.database().truncate(cart_item).launch();
        perform(post(create).param("shippingId", shippingId), ERROR, "购物车中没有选中的商品");
    }

    @Test
    public void testCreate_400_whenCartHasNoCheckedProduct() {
        this.database().launchCase("noCheckedProduct", cart_item);
        perform(post(create).param("shippingId", shippingId), ERROR, "购物车中没有选中的商品");
    }

    @Test
    public void testCreate_400_whenProductNotExist() {
        this.database().launchCase("productNotExist", cart_item, product);
        perform(post(create).param("shippingId", shippingId), NOT_FOUND, "不存在");
    }

    @Test
    public void testCreate_400_whenProductOffSale() {
        this.database().launchCase("productOffSale", cart_item, product);
        perform(post(create).param("shippingId", shippingId), NOT_FOUND, "已下架");
    }

    @Test
    public void testCreate_400_whenProductStockNotEnough() {
        this.database().launchCase("productStockNotEnough", cart_item, product);
        perform(post(create).param("shippingId", shippingId), ERROR, "库存不足");
    }

    @Test
    public void testCreate_200_andReturnOrderDetail() {
        this.database().truncate(order_item).launch();

        ResultActions createResult = perform(SUCCESS, post(create).param("shippingId", shippingId));

        assertEqualsOrderDetail(createResult);

        OrderVo actual = orderVoFromAndClean(createResult, true);
        OrderVo expected = orderVoFrom(EXPECTED_JSON_OF_ORDER_CREATE);
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testCreate_200_whileCleanCart() {
        assertCartItemCount(11);
        perform(SUCCESS, post(create).param("shippingId", shippingId));
        assertCartItemCount(1);
    }

    @Test
    public void testCreate_200_whileProductStockUpdated() {
        perform(SUCCESS, post(create).param("shippingId", shippingId));

        assertProductStock(96, productId);
        assertProductStock(0, productId2);
        assertProductStock(3, productId3);
    }

    /**
     * 取消订单
     * 404  找不到该订单
     * 403  无权限，该订单不属于当前用户
     * 400  取消失败：已发货 / 已完成 / 已关闭
     * 200  取消成功 (可取消的订单状态：已取消 / 未付款 / 已付款)
     * 200  取消成功，更新订单状态
     * 200  取消成功，恢复商品库存
     */
    @Test
    public void testCancel_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(cancel).param("orderNo", idNotExist));
    }

    @Test
    public void testCancel_403_whenOrderNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).perform(FORBIDDEN, post(cancel).param("orderNo", orderNo));
    }

    @Test
    public void testCancel_400_whenOrderStatusNotCorrect() {
        perform(ERROR, post(cancel).param("orderNo", orderShipped));
        perform(ERROR, post(cancel).param("orderNo", orderFinished));
        perform(ERROR, post(cancel).param("orderNo", orderClosed));
    }

    @Test
    public void testCancel_200_whenOrderStatusCorrect() {
        perform(SUCCESS, post(cancel).param("orderNo", orderCanceled));
        perform(SUCCESS, post(cancel).param("orderNo", orderUnPaid));
        perform(SUCCESS, post(cancel).param("orderNo", orderPaid));
    }

    @Test
    public void testCancel_200_whileOrderStatusUpdated() {
        perform(SUCCESS, post(cancel).param("orderNo", orderUnPaid));
        assertOrderStatus(CANCELED, detail, orderUnPaid);
    }

    @Test
    public void testCancel_200_whileProductStockRestored() {
        perform(SUCCESS, post(cancel).param("orderNo", orderNo));

        assertProductStock(101, productId);
        assertProductStock(7, productId2);
        assertProductStock(8, productId3);
    }

    /**
     * 支付订单
     * 404  找不到该订单
     * 403  无权限，该订单不属于当前用户
     * 400  支付失败：订单已支付 (除了未支付状态，都视作已支付)
     */
    @Test
    public void testPay_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(pay).param("orderNo", idNotExist));
    }

    @Test
    public void testPay_403_whenOrderNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).perform(FORBIDDEN, post(pay).param("orderNo", orderNo));
    }

    @Test
    public void testPay_400_whenOrderAlreadyPaid() {
        perform(ERROR, post(pay).param("orderNo", orderCanceled));
        perform(ERROR, post(pay).param("orderNo", orderPaid));
        perform(ERROR, post(pay).param("orderNo", orderShipped));
        perform(ERROR, post(pay).param("orderNo", orderFinished));
        perform(ERROR, post(pay).param("orderNo", orderClosed));
    }

    /**
     * 查询订单支付状态
     * 404  找不到该订单
     * 403  无权限，该订单不属于当前用户
     * 200  查询成功，返回订单支付状态
     */
    @Test
    public void testPaymentStatus_404_whenOrderNotExist() {
        perform(NOT_FOUND, post(payment_status).param("orderNo", idNotExist));
    }

    @Test
    public void testPaymentStatus_403_whenOrderNotBelongCurrentUser() {
        this.session(userId2, CONSUMER).perform(FORBIDDEN, post(payment_status).param("orderNo", orderNo));
    }

    @Test
    public void testPaymentStatus_200_withPaymentStatus() {
        assertTrue(wasPaid(perform(SUCCESS, post(payment_status).param("orderNo", orderCanceled))));
        assertTrue(wasPaid(perform(SUCCESS, post(payment_status).param("orderNo", orderPaid))));
        assertTrue(wasPaid(perform(SUCCESS, post(payment_status).param("orderNo", orderShipped))));
        assertTrue(wasPaid(perform(SUCCESS, post(payment_status).param("orderNo", orderFinished))));
        assertTrue(wasPaid(perform(SUCCESS, post(payment_status).param("orderNo", orderClosed))));

        assertFalse(wasPaid(perform(SUCCESS, post(payment_status).param("orderNo", orderUnPaid))));
    }

    private boolean wasPaid(ResultActions resultActions) {
        return dataFrom(resultActions);
    }

    private void assertCartItemCount(int expectedCount) {
        ResultActions resultActions = perform(SUCCESS, post(Apis.portal.cart.count));
        Integer actualCount = dataFrom(resultActions);
        assertEquals(expectedCount, actualCount.intValue());
    }

    private void assertEqualsOrderDetail(ResultActions createResult) {
        OrderVo orderVo = orderVoFrom(createResult);
        String orderNo = String.valueOf(orderVo.getOrderNo());

        ResultActions detailResult = perform(post(detail).param("orderNo", orderNo), SUCCESS);

        assertObjectEquals(content(createResult), content(detailResult));
    }

    private void assertProductStock(int expectedStock, String productId) {
        assertProductStock(expectedStock, Apis.portal.product.detail, productId);
    }
}
