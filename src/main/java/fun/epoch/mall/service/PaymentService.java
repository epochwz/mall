package fun.epoch.mall.service;

import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.QrCodeVo;

import java.util.Map;

public interface PaymentService {
    ServerResponse<QrCodeVo> preOrder(OrderVo order);

    ServerResponse<Object> callback(Map<String, String> params);
}
