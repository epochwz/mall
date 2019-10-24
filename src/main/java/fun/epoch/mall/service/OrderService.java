package fun.epoch.mall.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import fun.epoch.mall.common.Constant;
import fun.epoch.mall.common.Constant.OrderStatus;
import fun.epoch.mall.dao.*;
import fun.epoch.mall.entity.*;
import fun.epoch.mall.utils.DateTimeUtils;
import fun.epoch.mall.utils.response.ResponseCode;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.QrCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fun.epoch.mall.common.Constant.OrderStatus.*;
import static fun.epoch.mall.utils.response.ResponseCode.FORBIDDEN;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;

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

    public ServerResponse<OrderVo> detail(long orderNo) {
        return detail(orderMapper.selectByOrderNo(orderNo), null);
    }

    public ServerResponse<OrderVo> detail(int userId, long orderNo) {
        return detail(orderMapper.selectByOrderNo(orderNo), userId);
    }

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

    public ServerResponse<PageInfo<OrderVo>> search(Long orderNo, Integer userId, String keyword, Integer status, Long startTime, Long endTime, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> list = orderMapper.search(orderNo, userId, keyword, status, startTime, endTime);
        List<OrderVo> orderVos = list.stream().map(this::toOrderVo).collect(Collectors.toList());
        return ServerResponse.success(new PageInfo<>(orderVos));
    }

    public ServerResponse ship(long orderNo) {
        return updateOrderStatus(orderNo, SHIPPED.getCode(), CANCELED, UNPAID, SUCCESS, CLOSED);
    }

    public ServerResponse close(long orderNo) {
        return updateOrderStatus(orderNo, CLOSED.getCode(), CANCELED, PAID, SHIPPED, SUCCESS);
    }

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
            return ServerResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, errorMsg);
        }
        return ServerResponse.success();
    }

    public ServerResponse<OrderVo> preview(int userId) {
        ServerResponse<List<OrderItem>> toOrderItems = toOrderItems(userId);
        if (toOrderItems.isError()) {
            return ServerResponse.response(toOrderItems.getCode(), toOrderItems.getMsg());
        }

        List<OrderItem> items = toOrderItems.getData();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem item : items) {
            payment = payment.add(item.getTotalPrice());
        }

        OrderVo orderVo = OrderVo.builder()
                .payment(payment)
                .postage(new BigDecimal("0"))
                .products(items)
                .build();
        return ServerResponse.success(orderVo);
    }

    private ServerResponse<List<OrderItem>> toOrderItems(int userId) {
        List<CartItem> cartItems = cartItemMapper.selectCheckedItemsByUserId(userId);
        if (cartItems == null || cartItems.size() == 0) {
            return ServerResponse.error("购物车中没有选中的商品");
        }

        List<Integer> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toList());
        List<Product> products = productMapper.selectByPrimaryKeys(productIds);

        if (products.size() != cartItems.size()) {
            return ServerResponse.error(NOT_FOUND, "某商品不存在");
        }

        boolean productNotExist = products.stream().anyMatch(product -> product.getStatus() == Constant.SaleStatus.OFF_SALE);
        if (productNotExist) {
            return ServerResponse.error(NOT_FOUND, "某商品已下架");
        }

        cartItems.sort(Comparator.comparingInt(CartItem::getProductId));
        products.sort(Comparator.comparingInt(Product::getId));

        boolean productLimited = IntStream.range(0, products.size()).anyMatch(i -> products.get(i).getStock() < cartItems.get(i).getQuantity());
        if (productLimited) {
            return ServerResponse.error("某商品数量超过限制");
        }

        List<OrderItem> items = IntStream.range(0, products.size()).mapToObj(i -> toOrderItem(products.get(i), cartItems.get(i).getQuantity())).collect(Collectors.toList());

        return ServerResponse.success(items);
    }

    private OrderItem toOrderItem(Product product, int quantity) {
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));
        return OrderItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .totalPrice(totalPrice)
                .quantity(quantity)
                .productImage(imageHost + product.getMainImage())
                .build();
    }

    @Transactional
    public ServerResponse<OrderVo> create(int userId, int shippingId) {
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null) {
            return ServerResponse.error(NOT_FOUND, "收货地址不存在");
        }
        if (shipping.getUserId() != userId) {
            return ServerResponse.error(FORBIDDEN, "无权限，收货地址不属于当前用户");
        }

        ServerResponse<List<OrderItem>> toOrderItems = toOrderItems(userId);
        if (toOrderItems.isError()) {
            return ServerResponse.response(toOrderItems.getCode(), toOrderItems.getMsg());
        }

        List<OrderItem> items = toOrderItems.getData();
        for (OrderItem item : items) {
            Product product = productMapper.selectByPrimaryKey(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
            if (productMapper.updateSelectiveByPrimaryKey(product) == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ServerResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "更新商品库存失败");
            }
        }

        long orderNo = newOrderNo();
        items.forEach(orderItem -> {
            orderItem.setOrderNo(orderNo);
            if (orderItemMapper.insert(orderItem) != 1) {
                throw new RuntimeException("生成订单明细失败");
            }
        });

        if (cartItemMapper.deleteCheckedByUserId(userId) != items.size()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "清空购物车商品失败");
        }

        return ServerResponse.success();
    }

    private long newOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(100) % 10;
    }

    public ServerResponse cancel(int userId, long orderNo) {
        return null;
    }

    public ServerResponse<QrCodeVo> pay(int userId, long orderNo, int paymentType, int paymentPlatform) {
        return null;
    }

    public ServerResponse<Boolean> queryPaymentStatus(int userId, long orderNo) {
        return null;
    }
}
