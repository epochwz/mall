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

    // 合法值
    private static final Long orderNo = 1521421465877L;
}
