package fun.epoch.mall.service;

import fun.epoch.mall.common.Constant;
import fun.epoch.mall.dao.*;
import fun.epoch.mall.entity.*;
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
import static fun.epoch.mall.common.Constant.SaleStatus.OFF_SALE;
import static fun.epoch.mall.common.Constant.SaleStatus.ON_SALE;
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

    @Mock
    CartItemMapper cartItemMapper;

    @Mock
    ProductMapper productMapper;

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

    /**
     * 预览订单
     * <p>
     * 400  购物车中没有选中的商品
     * 404  某商品不存在 / 已下架
     * 400  某商品数量超过限制
     * 200  预览成功：返回订单预览信息
     */
    @Test
    public void testPreview_returnError_whenNoProductChecked() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.emptyList());
        testIfCodeEqualsError(service.preview(userId));
    }

    @Test
    public void testPreview_returnError_whenProductNotExist() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKeys(any())).thenReturn(Collections.emptyList());
        testIfCodeEqualsNotFound(service.preview(userId));
    }

    @Test
    public void testPreview_returnError_whenProductOffSale() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKeys(any())).thenReturn(Collections.singletonList(mockProduct.status(OFF_SALE).build()));
        testIfCodeEqualsNotFound(service.preview(userId));
    }

    @Test
    public void testPreview_returnError_whenProductQuantityLimited() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKeys(any())).thenReturn(Collections.singletonList(mockProduct.build()));
        testIfCodeEqualsError(service.preview(userId));
    }

    @Test
    public void testPreview_returnSuccess_withPreviewOrder() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKeys(any())).thenReturn(Collections.singletonList(mockProduct.stock(cartItem.getQuantity()).build()));

        ServerResponse<OrderVo> response = testIfCodeEqualsSuccess(service.preview(userId));

        OrderVo orderVo = response.getData();
        BigDecimal totalPrice = mockProduct.build().getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
        assertEquals(totalPrice, orderVo.getPayment());
        assertEquals(new BigDecimal("0"), orderVo.getPostage());

        OrderItem productItem = orderVo.getProducts().get(0);
        assertEquals(IMAGE_HOST + mainImage, productItem.getProductImage());
        assertEquals(totalPrice, productItem.getTotalPrice());
    }

    /**
     * 创建订单
     * <p>
     * 404  收货地址不存在
     * 403  该收货地址不属于当前用户
     * 400  购物车中没有选中的商品
     * 400  某商品不存在 / 已下架
     * 400  某商品数量超过限制
     * 200  创建成功：更新商品库存
     * 200  创建成功：创建订单，生成商品明细
     * 200  创建成功：清空购物车
     * 200  创建成功：返回订单信息
     */
    @Test
    public void testCreate_returnNotFound_whenShippingNotExist() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(null);
        testIfCodeEqualsNotFound(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnForbidden_whenShippingNotBelongCurrentUser() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(Shipping.builder().userId(otherUserId).build());
        testIfCodeEqualsForbidden(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnError_whenNoProductChecked() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.emptyList());
        testIfCodeEqualsError(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnError_whenProductNotExist() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKeys(any())).thenReturn(Collections.emptyList());
        testIfCodeEqualsNotFound(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnError_whenProductOffSale() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKeys(any())).thenReturn(Collections.singletonList(mockProduct.status(OFF_SALE).build()));
        testIfCodeEqualsNotFound(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnError_whenProductQuantityLimited() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKeys(any())).thenReturn(Collections.singletonList(mockProduct.build()));
        testIfCodeEqualsError(service.create(userId, shippingId));
    }

    // 合法值
    private static final long orderNo = 1521421465877L;

    private static final int userId = 1000000;

    private static final int shippingId = 1000000;
    private static final String shippingName = "梦无涯";

    private static final int productId = 1000000;
    private static final String productName = "斗破苍穹";
    private static final String mainImage = "image.jpg";
    private static final BigDecimal price = new BigDecimal("10.2");

    private static final String IMAGE_HOST = Constant.settings.get(Constant.SettingKeys.IMAGE_HOST);

    private static final Shipping shipping = Shipping.builder()
            .id(shippingId)
            .name(shippingName)
            .userId(userId)
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

    private static final CartItem cartItem = CartItem.builder()
            .productId(productId)
            .quantity(2)
            .build();

    private Product.ProductBuilder mockProduct = Product.builder()
            .id(productId)
            .name(productName)
            .price(price)
            .mainImage(mainImage)
            .status(ON_SALE)
            .stock(1);

    // 错误值
    private static final int otherUserId = 1000001;
}
