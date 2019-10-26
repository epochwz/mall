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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fun.epoch.mall.common.Constant.OrderStatus.*;
import static fun.epoch.mall.common.Constant.PaymentType.ONLINE_PAY;
import static fun.epoch.mall.common.Constant.SaleStatus.OFF_SALE;
import static fun.epoch.mall.common.Constant.SaleStatus.ON_SALE;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        doNothing().when(service).restoreProductStock(anyLong());

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
        when(productMapper.selectByPrimaryKey(anyInt())).thenReturn(null);
        testIfCodeEqualsNotFound(service.preview(userId));
    }

    @Test
    public void testPreview_returnError_whenProductOffSale() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKey(anyInt())).thenReturn(mockProduct.status(OFF_SALE).build());
        testIfCodeEqualsNotFound(service.preview(userId));
    }

    @Test
    public void testPreview_returnError_whenProductQuantityLimited() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKey(anyInt())).thenReturn(mockProduct.build());
        testIfCodeEqualsError(service.preview(userId));
    }

    @Test
    public void testPreview_returnSuccess_withPreviewOrder() {
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKey(anyInt())).thenReturn(mockProduct.stock(cartItem.getQuantity()).build());

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
        testIfCodeEqualsNotFound(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnError_whenProductOffSale() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKey(anyInt())).thenReturn(mockProduct.status(OFF_SALE).build());
        testIfCodeEqualsNotFound(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnError_whenProductQuantityLimited() {
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(Collections.singletonList(cartItem));
        when(productMapper.selectByPrimaryKey(anyInt())).thenReturn(mockProduct.build());
        testIfCodeEqualsError(service.create(userId, shippingId));
    }

    @Test
    public void testCreate_returnSuccess() {
        // stub shipping
        when(shippingMapper.selectByPrimaryKey(shippingId)).thenReturn(shipping);

        // stub cartItem
        List<CartItem> cartItems = Arrays.asList(
                CartItem.builder().productId(111).quantity(1).build(),
                CartItem.builder().productId(333).quantity(5).build(),
                CartItem.builder().productId(222).quantity(3).build()
        );
        when(cartItemMapper.selectCheckedItemsByUserId(userId)).thenReturn(cartItems);

        // stub product
        Product product1 = Product.builder().id(111).stock(10).price(price).status(ON_SALE).build();
        Product product2 = Product.builder().id(222).stock(8).price(price).status(ON_SALE).build();
        Product product3 = Product.builder().id(333).stock(6).price(price).status(ON_SALE).build();
        List<Product> products = Arrays.asList(product1, product2, product3);

        when(productMapper.selectByPrimaryKey(product1.getId())).thenReturn(product1);
        when(productMapper.selectByPrimaryKey(product2.getId())).thenReturn(product2);
        when(productMapper.selectByPrimaryKey(product3.getId())).thenReturn(product3);

        // stub product update
        when(productMapper.updateSelectiveByPrimaryKey(any())).thenReturn(1);

        // stub orderItem insert
        when(orderItemMapper.insert(any())).thenReturn(1);

        // stub empty cart
        when(cartItemMapper.deleteCheckedByUserId(userId)).thenReturn(cartItems.size());

        // stub insert order
        when(orderMapper.insert(any())).thenReturn(1);

        // execution
        service.create(userId, shippingId);

        // verify product update
        assertEquals(9, product1.getStock().intValue());
        assertEquals(5, product2.getStock().intValue());
        assertEquals(1, product3.getStock().intValue());
        verify(productMapper, times(products.size())).updateSelectiveByPrimaryKey(any());

        // verify orderItem insert
        verify(orderItemMapper, times(products.size())).insert(any());

        // verify empty cart
        verify(cartItemMapper).deleteCheckedByUserId(userId);

        // verify order insert
        verify(orderMapper).insert(any());

        // verify return order detail
        verify(service).detail(anyLong());
    }

    /**
     * 取消订单
     * <p>
     * 404  订单不存在
     * 403  该订单不属于当前用户
     * 400  取消失败：已发货 / 已完成 / 已关闭
     * 200  取消成功
     */
    @Test
    public void testCancel_returnNotFound_whenOrderNotExist() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(null);
        testIfCodeEqualsNotFound(service.cancel(userId, orderNo));
    }

    @Test
    public void testCancel_returnForbidden_whenOrderNotBelongCurrentUser() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);
        testIfCodeEqualsForbidden(service.cancel(otherUserId, orderNo));
    }

    @Test
    public void testCancel_returnError_whenOrderStatusNotValid() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(SHIPPED.getCode()).build());
        testIfCodeEqualsError(service.cancel(userId, orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(SUCCESS.getCode()).build());
        testIfCodeEqualsError(service.cancel(userId, orderNo));
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(CLOSED.getCode()).build());
        testIfCodeEqualsError(service.cancel(userId, orderNo));
    }

    @Test
    public void testCancel_returnSuccess_andThenSetStatusAsCancel_beforeCallMapper() {
        when(orderMapper.updateSelectiveByPrimaryKey(any())).thenAnswer((Answer<Integer>) invocation -> {
            Order order = invocation.getArgument(0);
            return order.getStatus() == CANCELED.getCode() ? 1 : 0;
        });

        doNothing().when(service).restoreProductStock(anyLong());

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(CANCELED.getCode()).build());
        testIfCodeEqualsSuccess(service.cancel(userId, orderNo));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(UNPAID.getCode()).build());
        testIfCodeEqualsSuccess(service.cancel(userId, orderNo));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(PAID.getCode()).build());
        testIfCodeEqualsSuccess(service.cancel(userId, orderNo));
    }

    /**
     * 预支付订单
     * <p>
     * 404  订单不存在
     * 403  无权限，订单不属于当前用户
     * 400  订单已支付 (除了未支付状态，其余订单状态均视为已支付)
     * 200  预支付成功，返回订单支付二维码
     */
    @Test
    public void testPay_returnNotFound_whenOrderNotExist() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(null);
        testIfCodeEqualsNotFound(service.pay(userId, orderNo, 1, 1));
    }

    @Test
    public void testPay_returnForbidden_whenOrderNotBelongCurrentUser() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);
        testIfCodeEqualsForbidden(service.pay(otherUserId, orderNo, 1, 1));
    }

    @Test
    public void testPay_returnError_whenOrderAlreadyPaid() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(CANCELED.getCode()).build());
        testIfCodeEqualsError(service.pay(userId, orderNo, 1, 1));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(PAID.getCode()).build());
        testIfCodeEqualsError(service.pay(userId, orderNo, 1, 1));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(SHIPPED.getCode()).build());
        testIfCodeEqualsError(service.pay(userId, orderNo, 1, 1));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(SUCCESS.getCode()).build());
        testIfCodeEqualsError(service.pay(userId, orderNo, 1, 1));

        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(CLOSED.getCode()).build());
        testIfCodeEqualsError(service.pay(userId, orderNo, 1, 1));
    }

    /**
     * 查询订单支付状态
     * <p>
     * 404  订单不存在
     * 403  无权限，订单不属于当前用户
     * 200  查询成功，返回订单支付状态
     */
    @Test
    public void testQueryPaymentStatus_returnNotFound_whenOrderNotExist() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(null);
        testIfCodeEqualsNotFound(service.queryPaymentStatus(userId, orderNo));
    }

    @Test
    public void testQueryPaymentStatus_returnForbidden_whenOrderNotBelongCurrentUser() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);
        testIfCodeEqualsForbidden(service.queryPaymentStatus(otherUserId, orderNo));
    }

    @Test
    public void testQueryPaymentStatus_returnSuccess_whenOrderPaid() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);
        ServerResponse<Boolean> response = testIfCodeEqualsSuccess(service.queryPaymentStatus(userId, orderNo));
        assertTrue(response.getData());
    }

    @Test
    public void testQueryPaymentStatus_returnSuccess_whenOrderUnPaid() {
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(Order.builder().userId(userId).status(UNPAID.getCode()).build());
        ServerResponse<Boolean> response = testIfCodeEqualsSuccess(service.queryPaymentStatus(userId, orderNo));
        assertFalse(response.getData());
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
