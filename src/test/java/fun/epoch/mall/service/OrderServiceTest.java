package fun.epoch.mall.service;

import fun.epoch.mall.dao.OrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsNotFound;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
    @InjectMocks
    OrderService service;

    @Mock
    OrderMapper orderMapper;

    /**
     * 查看订单详情
     * <p>
     * 404  订单不存在
     * 200  查看成功，返回订单详情
     */
    @Test
    public void testDetail_returnNotFound_whenOrderNoExist() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(null);
        testIfCodeEqualsNotFound(service.detail(orderNo));
    }

    private static final long orderNo = 1521421465877L;
}
