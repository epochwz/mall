package fun.epoch.mall.controller.manage;

import fun.epoch.mall.service.OrderService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManageOrderControllerTest {
    @InjectMocks
    ManageOrderController controller;

    @Mock
    OrderService service;

    /**
     * 查看订单详情
     * <p>
     * 200  查看成功：调用 service 成功
     */
    @Test
    public void testDetail_returnSuccess_whenCallServiceSuccess() {
        when(service.detail(orderNo)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.detail(orderNo));
    }

    /**
     * 搜索订单
     * <p>
     * 200  搜索成功：调用 service 成功
     */
    @Test
    public void testSearch_returnSuccess_whenCallServiceSuccess() {
        when(service.search(orderNo, userId, keyword, status, startTime, endTime, 1, 5)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.search(orderNo, userId, status, startTime, endTime, 1, 5));
    }

    /**
     * 订单发货
     * <p>
     * 200  发货成功：调用 service 成功
     */
    @Test
    public void testShip_returnSuccess_whenCallServiceSuccess() {
        when(service.ship(orderNo)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.ship(orderNo));
    }

    /**
     * 关闭订单
     * <p>
     * 200  关闭成功：调用 service 成功
     */
    @Test
    public void testClose_returnSuccess_whenCallServiceSuccess() {
        when(service.close(orderNo)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.close(orderNo));
    }

    // 合法值
    private static final Long orderNo = 1521421465877L;
    private static final Integer userId = 1000000;
    private static final String keyword = null;
    private static final Integer status = 10;
    private static final Long startTime = 1521421465877L;
    private static final Long endTime = 1521421465877L;
}
