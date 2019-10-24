package fun.epoch.mall.service;

import fun.epoch.mall.entity.Order;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.QrCodeVo;

public interface PaymentService {
    ServerResponse<QrCodeVo> preOrder(Order order);
}
