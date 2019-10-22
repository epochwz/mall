package fun.epoch.mall.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import fun.epoch.mall.common.Constant;
import fun.epoch.mall.common.Constant.OrderStatus;
import fun.epoch.mall.dao.OrderItemMapper;
import fun.epoch.mall.dao.OrderMapper;
import fun.epoch.mall.dao.ShippingMapper;
import fun.epoch.mall.entity.Order;
import fun.epoch.mall.entity.OrderItem;
import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.utils.DateTimeUtils;
import fun.epoch.mall.utils.response.ResponseCode;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.QrCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static fun.epoch.mall.common.Constant.OrderStatus.*;
import static fun.epoch.mall.utils.response.ResponseCode.FORBIDDEN;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;

@Service
public class OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ShippingMapper shippingMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

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
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) return ServerResponse.error(NOT_FOUND, "找不到订单");

        if (order.getStatus() == CANCELED.getCode()
                || order.getStatus() == UNPAID.getCode()
                || order.getStatus() == SUCCESS.getCode()
                || order.getStatus() == CLOSED.getCode()
        ) {
            return ServerResponse.error(String.format("订单状态不合适，当前订单状态是 [%s], 不允许发货!", OrderStatus.valueOf(order.getStatus())));
        }

        order.setStatus(SHIPPED.getCode());
        if (orderMapper.updateSelectiveByPrimaryKey(order) > 0) {
            return ServerResponse.success();
        }
        return ServerResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "发货失败");
    }

    public ServerResponse close(long orderNo) {
        return null;
    }

    public ServerResponse preview(int userId) {
        return null;
    }

    public ServerResponse<OrderVo> create(int userId, int shippingId) {
        return null;
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
