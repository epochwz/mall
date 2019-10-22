package fun.epoch.mall.service;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.dao.OrderMapper;
import fun.epoch.mall.entity.Order;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.QrCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;

@Service
public class OrderService {
    @Autowired
    OrderMapper orderMapper;

    public ServerResponse<OrderVo> detail(long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.error(NOT_FOUND, "找不到订单");
        }
        return null;
    }

    public ServerResponse<OrderVo> detail(int userId, long orderNo) {
        return null;
    }

    public ServerResponse<PageInfo<OrderVo>> search(Long orderNo, Integer userId, String keyword, Integer status, Long startTime, Long endTime, int pageNum, int pageSize) {
        return null;
    }

    public ServerResponse ship(long orderNo) {
        return null;
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
