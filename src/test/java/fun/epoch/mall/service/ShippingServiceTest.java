package fun.epoch.mall.service;

import fun.epoch.mall.dao.ShippingMapper;
import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShippingServiceTest {
    @InjectMocks
    ShippingService service;

    @Mock
    ShippingMapper mapper;

    /**
     * 添加收货地址
     * <p>
     * 200  添加成功，返回新增收货地址的 id
     */
    @Test
    public void testAddShipping_returnSuccess_withShippingId() {
        when(mapper.insert(any())).thenAnswer((Answer<Integer>) invocation -> {
            Shipping shipping = invocation.getArgument(0);
            shipping.setId(shippingId);
            return 1;
        });

        ServerResponse<Integer> response = testIfCodeEqualsSuccess(service.add(mock().build()));
        assertEquals(shippingId, response.getData());
    }

    // 合法值
    private static final Integer userId = 1000000;
    private static final Integer shippingId = 1000000;

    private Shipping.ShippingBuilder mock() {
        return Shipping.builder().userId(userId).name("小明").mobile("15623336666").province("广东省").city("广州市").district("小谷围街道").address("宇宙工业大学");
    }
}
