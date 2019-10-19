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

import static fun.epoch.mall.common.enhanced.TestHelper.*;
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

    /**
     * 删除收货地址
     * <p>
     * 404  收货地址不存在
     * 403  无权限 (收货地址不属于当前用户)
     * 200  删除成功
     */
    @Test
    public void testDeleteShipping_returnNotFound_whenShippingNotExist() {
        when(mapper.selectByPrimaryKey(shippingId)).thenReturn(null);
        testIfCodeEqualsNotFound(service.delete(userId, shippingId));
    }

    @Test
    public void testDeleteShipping_returnForbidden_whenShippingNotBelongCurrentUser() {
        when(mapper.selectByPrimaryKey(shippingId)).thenReturn(mock().userId(otherUserId).build());
        testIfCodeEqualsForbidden(service.delete(userId, shippingId));
    }

    @Test
    public void testDeleteShipping_returnSuccess() {
        when(mapper.selectByPrimaryKey(shippingId)).thenReturn(mock().build());
        when(mapper.deleteByPrimaryKey(shippingId)).thenReturn(1);
        testIfCodeEqualsSuccess(service.delete(userId, shippingId));
    }

    /**
     * 修改收货地址
     * <p>
     * 404  收货地址不存在
     * 403  无权限 (收货地址不属于当前用户)
     * 200  修改成功，返回修改后的收货地址
     */
    @Test
    public void testUpdateShipping_returnNotFound_whenShippingNotExist() {
        when(mapper.selectByPrimaryKey(shippingId)).thenReturn(null);
        testIfCodeEqualsNotFound(service.update(mock().id(shippingId).build()));
    }

    @Test
    public void testUpdateShipping_returnForbidden_whenShippingNotBelongCurrentUser() {
        when(mapper.selectByPrimaryKey(shippingId)).thenReturn(mock().userId(otherUserId).build());
        testIfCodeEqualsForbidden(service.update(mock().id(shippingId).build()));
    }

    @Test
    public void testUpdateShipping_returnSuccess_withNewShipping() {
        Shipping dbShipping = mock().build();
        when(mapper.selectByPrimaryKey(shippingId)).thenReturn(dbShipping);
        when(mapper.updateSelectiveByPrimaryKey(any())).thenAnswer((Answer<Integer>) invocation -> {
            Shipping shipping = invocation.getArgument(0);
            dbShipping.setName(shipping.getName());
            return 1;
        });

        Shipping updateShipping = mock().id(shippingId).name(newShippingName).build();
        ServerResponse<Shipping> response = testIfCodeEqualsSuccess(service.update(updateShipping));
        assertObjectEquals(dbShipping, response.getData());
    }

    // 合法值
    private static final Integer userId = 1000000;
    private static final Integer shippingId = 1000000;

    private static final String newShippingName = "epoch";

    private Shipping.ShippingBuilder mock() {
        return Shipping.builder().userId(userId).name("小明").mobile("15623336666").province("广东省").city("广州市").district("小谷围街道").address("宇宙工业大学");
    }

    // 错误值
    private static final Integer otherUserId = 1000001;
}
