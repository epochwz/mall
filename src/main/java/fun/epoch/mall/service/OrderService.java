package fun.epoch.mall.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import fun.epoch.mall.common.Constant;
import fun.epoch.mall.common.Constant.OrderStatus;
import fun.epoch.mall.dao.*;
import fun.epoch.mall.entity.*;
import fun.epoch.mall.exception.OrderCreateException;
import fun.epoch.mall.exception.OrderUpdateException;
import fun.epoch.mall.service.pay.AlipayService;
import fun.epoch.mall.utils.DateTimeUtils;
import fun.epoch.mall.utils.PageUtils;
import fun.epoch.mall.utils.TextUtils;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.ProductVo;
import fun.epoch.mall.vo.QrCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static fun.epoch.mall.common.Constant.OrderStatus.valueOf;
import static fun.epoch.mall.common.Constant.OrderStatus.*;
import static fun.epoch.mall.common.Constant.PaymentPlatform.ALIPAY;
import static fun.epoch.mall.common.Constant.PaymentType.ONLINE_PAY;
import static fun.epoch.mall.common.Constant.SaleStatus.OFF_SALE;
import static fun.epoch.mall.utils.response.ResponseCode.*;

@Service
public class OrderService {
    String imageHost = Constant.settings.get(Constant.SettingKeys.IMAGE_HOST);

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ShippingMapper shippingMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    CartItemMapper cartItemMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    AlipayService alipayService;

    public ServerResponse<OrderVo> detail(long orderNo) {
        return detail(orderMapper.selectByOrderNo(orderNo), null);
    }

    public ServerResponse<OrderVo> detail(int userId, long orderNo) {
        return detail(orderMapper.selectByOrderNo(orderNo), userId);
    }

    public ServerResponse<PageInfo<OrderVo>> search(Long orderNo, Integer userId, String keyword, Integer status, Long startTime, Long endTime, int pageNum, int pageSize) {
        PageInfo<Order> page = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> orderMapper.search(orderNo, userId, keyword, status, startTime, endTime)
        );
        List<OrderVo> orderVos = page.getList().stream().map(this::toOrderVo).collect(Collectors.toList());
        return ServerResponse.success(PageUtils.convert(page, orderVos));
    }

    public ServerResponse ship(long orderNo) {
        return updateOrderStatus(orderNo, SHIPPED.getCode(), CANCELED, UNPAID, OrderStatus.SUCCESS, CLOSED);
    }

    @Transactional
    public ServerResponse close(long orderNo) {
        ServerResponse updateOrderStatus = updateOrderStatus(orderNo, CLOSED.getCode(), CANCELED, PAID, SHIPPED, OrderStatus.SUCCESS);
        if (updateOrderStatus.isSuccess()) {
            try {
                restoreProductStock(orderNo);
            } catch (OrderUpdateException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ServerResponse.error(INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        return updateOrderStatus;
    }

    public ServerResponse<OrderVo> preview(int userId) {
        ServerResponse<List<OrderItem>> generateOrderItems = generateOrderItems(userId, null);
        if (generateOrderItems.isError()) return ServerResponse.response(generateOrderItems);

        List<OrderItem> orderItems = generateOrderItems.getData();

        OrderVo orderVo = OrderVo.builder()
                .payment(countPayment(orderItems))
                .postage(new BigDecimal("0"))
                .products(orderItems)
                .build();
        return ServerResponse.success(orderVo);
    }

    public ServerResponse<OrderVo> create(int userId, int shippingId) {
        ServerResponse<OrderVo> checkShipping = checkShipping(userId, shippingId);
        if (checkShipping.isError()) return checkShipping;

        long orderNo = newOrderNo();

        ServerResponse<List<OrderItem>> generateOrderItems = generateOrderItems(userId, orderNo);
        if (generateOrderItems.isError()) return ServerResponse.response(generateOrderItems);

        List<OrderItem> orderItems = generateOrderItems.getData();

        Order order = Order.builder()
                .userId(userId)
                .shippingId(shippingId)
                .orderNo(orderNo)
                .payment(countPayment(orderItems))
                .postage(new BigDecimal("0"))
                .status(UNPAID.getCode())
                .paymentType(ONLINE_PAY.getCode())
                .build();

        return createOrder(order, orderItems);
    }

    @Transactional
    public ServerResponse cancel(int userId, long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.error(NOT_FOUND, "找不到订单");
        }
        if (order.getUserId() != userId) {
            return ServerResponse.error(FORBIDDEN, "无权限，该订单不属于当前用户");
        }
        ServerResponse updateOrderStatus = updateOrderStatus(orderNo, CANCELED.getCode(), SHIPPED, OrderStatus.SUCCESS, CLOSED);
        if (updateOrderStatus.isSuccess()) {
            try {
                restoreProductStock(orderNo);
            } catch (OrderUpdateException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ServerResponse.error(INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        return updateOrderStatus;
    }

    public ServerResponse<QrCodeVo> pay(int userId, long orderNo, int paymentType, int paymentPlatform) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.error(NOT_FOUND, "找不到订单");
        }
        if (order.getUserId() != userId) {
            return ServerResponse.error(FORBIDDEN, "无权限，该订单不属于当前用户");
        }
        if (order.getStatus() != UNPAID.getCode()) {
            return ServerResponse.error("订单已支付");
        }
        PaymentService paymentService = choosePaymentService(paymentType, paymentPlatform);
        return paymentService.preOrder(toOrderVo(order));
    }

    private PaymentService choosePaymentService(int paymentType, int paymentPlatform) {
        if (paymentType == ONLINE_PAY.getCode() && paymentPlatform == ALIPAY.getCode()) {
            return alipayService;
        }
        return alipayService;
    }

    public ServerResponse<Boolean> queryPaymentStatus(int userId, long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.error(NOT_FOUND, "找不到订单");
        }
        if (order.getUserId() != userId) {
            return ServerResponse.error(FORBIDDEN, "无权限，该订单不属于当前用户");
        }
        return ServerResponse.success(order.getStatus() != UNPAID.getCode());
    }

    /* ****************************** 查询订单 开始  ****************************** */
    private ServerResponse<OrderVo> detail(Order order, Integer userId) {
        if (order == null) return ServerResponse.error(NOT_FOUND, "找不到订单");
        if (userId != null && !userId.equals(order.getUserId())) return ServerResponse.error(FORBIDDEN, "无权限，该订单不属于当前用户");
        return ServerResponse.success(toOrderVo(order));
    }

    OrderVo toOrderVo(Order order) {
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        List<OrderItem> products = orderItemMapper.selectByOrderNo(order.getOrderNo());

        return toOrderVo(order, shipping, products);
    }

    private OrderVo toOrderVo(Order order, Shipping shipping, List<OrderItem> products) {
        return OrderVo.builder()
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())

                .shipping(shipping)
                .products(products)

                .payment(order.getPayment())
                .postage(order.getPostage())

                .paymentType(order.getPaymentType())
                .paymentTypeDesc(Constant.PaymentType.valueOf(order.getPaymentType()))

                .status(order.getStatus())
                .statusDesc(OrderStatus.valueOf(order.getStatus()))

                .createTime(DateTimeUtils.format(order.getCreateTime()))
                .paymentTime(DateTimeUtils.format(order.getPaymentTime()))
                .shipTime(DateTimeUtils.format(order.getSendTime()))
                .endTime(DateTimeUtils.format(order.getEndTime()))
                .closeTime(DateTimeUtils.format(order.getCloseTime()))

                .build();
    }

    /* ****************************** 更新订单 开始  ****************************** */
    private ServerResponse updateOrderStatus(long orderNo, int expectedStatus, OrderStatus... unexpectedStatus) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) return ServerResponse.error(NOT_FOUND, "找不到订单");

        if (unexpectedStatus != null && unexpectedStatus.length > 0) {
            for (OrderStatus status : unexpectedStatus) {
                if (status.getCode() == order.getStatus()) {
                    String errorMsg = String.format("订单状态变更失败：[%s] --> [%s], 订单状态不合适", valueOf(order.getStatus()), valueOf(expectedStatus));
                    return ServerResponse.error(errorMsg);
                }
            }
        }

        order.setStatus(expectedStatus);
        if (orderMapper.updateSelectiveByPrimaryKey(order) == 0) {
            String errorMsg = String.format("订单状态变更失败：[%s] --> [%s]", valueOf(order.getStatus()), valueOf(expectedStatus));
            return ServerResponse.error(INTERNAL_SERVER_ERROR, errorMsg);
        }
        return ServerResponse.success();
    }

    void restoreProductStock(long orderNo) {
        OrderVo orderVo = detail(orderNo).getData();
        orderVo.getProducts().forEach(item -> {
            Product product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                if (productMapper.updateSelectiveByPrimaryKey(product) != 1) {
                    throw new OrderUpdateException(String.format("恢复商品[%s]库存失败", product.getId()));
                }
            }
        });
    }

    /* ****************************** 预览订单 开始  ****************************** */
    private ServerResponse<List<OrderItem>> generateOrderItems(int userId, Long orderNo) {
        List<CartItem> cartItems = cartItemMapper.selectCheckedItemsByUserId(userId);
        if (cartItems == null || cartItems.size() == 0) {
            return ServerResponse.error("购物车中没有选中的商品");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            ServerResponse<List<OrderItem>> checkProduct = checkProduct(cartItem, product);
            if (checkProduct.isError()) return checkProduct;
            orderItems.add(toOrderItem(orderNo, product, cartItem.getQuantity()));
        }

        return ServerResponse.success(orderItems);
    }

    private ServerResponse<List<OrderItem>> checkProduct(CartItem cartItem, Product product) {
        if (product == null) {
            return ServerResponse.error(NOT_FOUND, String.format("商品 [%s] 不存在", cartItem.getProductId()));
        }
        if (product.getStatus() == OFF_SALE) {
            return ServerResponse.error(NOT_FOUND, String.format("商品 [%s] 已下架", product.getId()));
        }
        if (product.getStock() < cartItem.getQuantity()) {
            return ServerResponse.error(String.format("商品 [%s] 数量超过限制：库存不足", product.getId()));
        }
        return ServerResponse.success();
    }

    private OrderItem toOrderItem(Long orderNo, Product product, int quantity) {
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));
        String mainImage = ProductVo.extractMainImage(product);
        String productImage = TextUtils.isNotBlank(mainImage) ? imageHost + mainImage : "";
        return OrderItem.builder()
                .orderNo(orderNo)
                .productId(product.getId())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .totalPrice(totalPrice)
                .quantity(quantity)
                .productImage(productImage)
                .build();
    }

    /* ****************************** 创建订单 开始  ****************************** */
    private ServerResponse<OrderVo> checkShipping(int userId, int shippingId) {
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null) {
            return ServerResponse.error(NOT_FOUND, "收货地址不存在");
        }
        if (shipping.getUserId() != userId) {
            return ServerResponse.error(FORBIDDEN, "无权限，收货地址不属于当前用户");
        }
        return ServerResponse.success();
    }

    private BigDecimal countPayment(List<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal::add).orElse(new BigDecimal("0"));
    }

    private long newOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(100) % 10;
    }

    @Transactional
    ServerResponse<OrderVo> createOrder(Order order, List<OrderItem> orderItems) {
        try {
            this
                    .insertOrder(order)
                    .insertOrderItem(orderItems)
                    .updateProductStock(orderItems)
                    .cleanCart(order, orderItems);
            return detail(order.getOrderNo());
        } catch (OrderCreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.error(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private OrderService insertOrder(Order order) {
        if (orderMapper.insert(order) != 1) {
            throw new OrderCreateException("创建订单失败：生成订单失败");
        }
        return this;
    }

    private OrderService insertOrderItem(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            if (orderItemMapper.insert(orderItem) != 1) {
                throw new OrderCreateException("创建订单失败：生成订单明细失败");
            }
        });
        return this;
    }

    private OrderService updateProductStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = productMapper.selectByPrimaryKey(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
            if (productMapper.updateSelectiveByPrimaryKey(product) != 1) {
                throw new OrderCreateException("创建订单失败：更新商品库存失败");
            }
        }
        return this;
    }

    private OrderService cleanCart(Order order, List<OrderItem> orderItems) {
        if (cartItemMapper.deleteCheckedByUserId(order.getUserId()) != orderItems.size()) {
            throw new OrderCreateException("创建订单失败：清空购物车商品失败");
        }
        return this;
    }
}
