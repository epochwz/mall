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
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fun.epoch.mall.common.Constant.OrderStatus.*;
import static fun.epoch.mall.common.Constant.PaymentType.ONLINE_PAY;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
    @InjectMocks
    @Spy
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
     * 403  订单不属于当前用户
     * 200  查看成功，返回订单详情
     */
    @Test
    public void testDetail_returnNotFound_whenOrderNoExist() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(null);
        testIfCodeEqualsNotFound(service.detail(orderNo));
    }

    @Test
    public void testDetail_returnForbidden_whenOrderNoBelongCurrentUser() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);
        testIfCodeEqualsForbidden(service.detail(otherUserId, orderNo));
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

    /**
     * 搜索订单
     * <p>
     * 200  搜索成功，返回订单列表
     */
    @Test
    public void testSearch_returnSuccess() {
        int size = new Random().nextInt(10);
        List<Order> orders = IntStream.range(0, size).mapToObj(i -> order).collect(Collectors.toList());

        when(orderMapper.search(null, null, null, null, null, null)).thenReturn(orders);
        testIfCodeEqualsSuccess(service.search(null, null, null, null, null, null, 1, 5));
        Mockito.verify(service, times(orders.size())).toOrderVo(any());
    }

    /**
     * 订单发货
     * <p>
     * 404  订单不存在
     * 400  发货失败：已取消 / 未付款  / 已完成 / 已关闭
     * 200  发货成功：已付款 / 已发货 (且调用 mapper 之前必须将订单状态设置成已发货)
     */
    @Test
    public void testShip_returnNotFound_whenOrderNotExist() {
        testIfCodeEqualsNotFound(service.ship(orderNo));
    }

    @Test
    public void testShip_returnError_whenOrderStatusNotValid() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(CANCELED.getCode()).build());
        testIfCodeEqualsError(service.ship(orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(UNPAID.getCode()).build());
        testIfCodeEqualsError(service.ship(orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(SUCCESS.getCode()).build());
        testIfCodeEqualsError(service.ship(orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(CLOSED.getCode()).build());
        testIfCodeEqualsError(service.ship(orderNo));
    }

    @Test
    public void testShip_returnSuccess_andThenSetStatusAsShipped_beforeCallMapper() {
        when(orderMapper.updateSelectiveByPrimaryKey(any())).thenAnswer((Answer<Integer>) invocation -> {
            Order order = invocation.getArgument(0);
            return order.getStatus() == SHIPPED.getCode() ? 1 : 0;
        });

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(PAID.getCode()).build());
        testIfCodeEqualsSuccess(service.ship(orderNo));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(SHIPPED.getCode()).build());
        testIfCodeEqualsSuccess(service.ship(orderNo));
    }

    /**
     * 关闭订单
     * <p>
     * 404  订单不存在
     * 400  关闭失败：已取消 / 已付款 / 已发货 / 已完成
     * 200  关闭成功：未付款 / 已关闭 (且调用 mapper 之前必须将订单状态设置成已关闭)
     */
    @Test
    public void testClose_returnNotFound_whenOrderNotExist() {
        testIfCodeEqualsNotFound(service.close(orderNo));
    }

    @Test
    public void testClose_returnError_whenOrderStatusNotValid() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(CANCELED.getCode()).build());
        testIfCodeEqualsError(service.close(orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(PAID.getCode()).build());
        testIfCodeEqualsError(service.close(orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(SHIPPED.getCode()).build());
        testIfCodeEqualsError(service.close(orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(SUCCESS.getCode()).build());
        testIfCodeEqualsError(service.close(orderNo));
    }

    @Test
    public void testClose_returnSuccess_onlyWhenOrderStatusIsValid_and_setStatusAsClosed_beforeCallMapper() {
        when(orderMapper.updateSelectiveByPrimaryKey(any())).thenAnswer((Answer<Integer>) invocation -> {
            Order order = invocation.getArgument(0);
            return order.getStatus() == CLOSED.getCode() ? 1 : 0;
        });

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(UNPAID.getCode()).build());
        testIfCodeEqualsSuccess(service.close(orderNo));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().status(CLOSED.getCode()).build());
        testIfCodeEqualsSuccess(service.close(orderNo));
    }

    // 合法值
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

    // 错误值
    private static final int otherUserId = 1000001;
}
