package fun.epoch.mall.service;

import fun.epoch.mall.dao.OrderItemMapper;
import fun.epoch.mall.dao.OrderMapper;
import fun.epoch.mall.dao.ShippingMapper;
import fun.epoch.mall.entity.Order;
import fun.epoch.mall.entity.OrderItem;
import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.utils.DateTimeUtils;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import static fun.epoch.mall.common.Constant.OrderStatus.PAID;
import static fun.epoch.mall.common.Constant.PaymentType.ONLINE_PAY;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsNotFound;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
    @InjectMocks
    OrderService service;

    @Mock
    OrderMapper orderMapper;

    @Mock
    ShippingMapper shippingMapper;

    @Mock
    OrderItemMapper orderItemMapper;

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

    @Test
    public void testDetail_returnSuccess_withOrderVo() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);
        when(orderItemMapper.selectByOrderNo(orderNo)).thenReturn(Collections.singletonList(orderItem));

        ServerResponse<OrderVo> response = testIfCodeEqualsSuccess(service.detail(orderNo));

        OrderVo orderVo = response.getData();

        assertEquals(order.getPayment(), orderVo.getPayment());
        assertEquals(PAID.getDesc(), orderVo.getStatusDesc());
        assertEquals(ONLINE_PAY.getDesc(), orderVo.getPaymentTypeDesc());
        assertEquals(order.getShippingId(), orderVo.getShipping().getId());
        assertEquals(DateTimeUtils.format(order.getCreateTime()), orderVo.getCreateTime());
        assertEquals(DateTimeUtils.format(order.getPaymentTime()), orderVo.getPaymentTime());

        Shipping shipping = orderVo.getShipping();
        assertEquals(shippingName, shipping.getName());

        OrderItem orderItem = orderVo.getProducts().get(0);
        assertEquals(productName, orderItem.getProductName());
    }

    private static final long orderNo = 1521421465877L;

    private static final int userId = 1000000;

    private static final int shippingId = 1000000;
    private static final String shippingName = "梦无涯";

    private static final int productId = 1000000;
    private static final String productName = "斗破苍穹";

    private static final Shipping shipping = Shipping.builder()
            .id(shippingId)
            .name(shippingName)
            .build();

    private static final OrderItem orderItem = OrderItem.builder()
            .orderNo(orderNo)
            .productId(productId)
            .productName(productName)
            .unitPrice(new BigDecimal("10.2"))
            .quantity(3)
            .totalPrice(new BigDecimal("30.6"))
            .build();

    private static final Order order = Order.builder()
            .userId(userId)
            .orderNo(orderNo)
            .payment(orderItem.getTotalPrice())
            .postage(new BigDecimal("0"))
            .status(PAID.getCode())
            .paymentType(ONLINE_PAY.getCode())
            .paymentTime(new Date())
            .createTime(new Date())
            .shippingId(shipping.getId())
            .build();
}
