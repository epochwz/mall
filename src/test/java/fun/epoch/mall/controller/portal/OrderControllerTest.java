package fun.epoch.mall.controller.portal;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.OrderService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEquals;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_IMPLEMENTED;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @InjectMocks
    OrderController controller;

    @Mock
    OrderService service;

    @Mock
    MockHttpSession session;

    @Before
    public void setup() {
        when(session.getAttribute(CURRENT_USER)).thenReturn(User.builder().id(userId).build());
    }

    /**
     * 查看订单详情
     * <p>
     * 200  查看成功：调用 service 成功
     */
    @Test
    public void testDetail_returnSuccess_whenCallServiceSuccess() {
        when(service.detail(userId, orderNo)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.detail(session, orderNo));
    }

    /**
     * 搜索订单
     * <p>
     * 200  搜索成功：调用 service 成功
     */
    @Test
    public void testSearch_returnSuccess_whenCallServiceSuccess() {
        when(service.search(orderNo, userId, keyword, status, startTime, endTime, 1, 5)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.search(session, orderNo, keyword, status, startTime, endTime, 1, 5));
    }

    /**
     * 预览订单
     * <p>
     * 200  预览成功：调用 service 成功
     */
    @Test
    public void testPreview_returnSuccess_whenCallServiceSuccess() {
        when(service.preview(userId)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.preview(session));
    }

    /**
     * 创建订单
     * <p>
     * 200  创建成功：调用 service 成功
     */
    @Test
    public void testCreate_returnSuccess_whenCallServiceSuccess() {
        when(service.create(userId, shippingId)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.create(session, shippingId));
    }

    /**
     * 取消订单
     * <p>
     * 200  取消成功：调用 service 成功
     */
    @Test
    public void testCancel_returnSuccess_whenCallServiceSuccess() {
        when(service.cancel(userId, orderNo)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.cancel(session, orderNo));
    }

    /**
     * 支付订单
     * <p>
     * 501  非法参数：支付类型非空时不是系统支持的支付类型
     * 501  非法参数：支付平台非空时不是系统支持的支付平台
     * 200  支付成功：调用 service 成功
     */
    @Test
    public void testPay_returnNotImplemented_whenPaymentTypeNotSupported() {
        testIfCodeEquals(NOT_IMPLEMENTED, controller.pay(session, orderNo, paymentType, notSupported));
    }

    @Test
    public void testPay_returnNotImplemented_whenPaymentPlatformNotSupported() {
        testIfCodeEquals(NOT_IMPLEMENTED, controller.pay(session, orderNo, paymentType, notSupported));
    }

    @Test
    public void testPay_returnSuccess_whenCallServiceSuccess() {
        when(service.pay(userId, orderNo, paymentType, paymentPlatform)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.pay(session, orderNo, paymentType, paymentPlatform));
    }

    /**
     * 查询订单支付状态
     * <p>
     * 200  查询成功：调用 service 成功
     */
    @Test
    public void testQueryPaymentStatus_returnSuccess_whenCallServiceSuccess() {
        when(service.queryPaymentStatus(userId, orderNo)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.queryPaymentStatus(session, orderNo));
    }

    // 合法值
    private static final Long orderNo = 1521421465877L;
    private static final String keyword = "瓜子";
    private static final Integer userId = 1000000;
    private static final Integer status = 10;
    private static final Long startTime = 1521421465877L;
    private static final Long endTime = 1521421465877L;

    private static final Integer shippingId = 1;

    private static final int paymentType = 1;
    private static final int paymentPlatform = 1;
    private static final int notSupported = 999;
}
