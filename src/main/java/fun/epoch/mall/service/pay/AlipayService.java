package fun.epoch.mall.service.pay;

import fun.epoch.mall.entity.Order;
import fun.epoch.mall.service.PaymentService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.QrCodeVo;
import org.springframework.stereotype.Service;

@Service
public class AlipayService implements PaymentService {
    @Override
    public ServerResponse<QrCodeVo> preOrder(Order order) {
        return null;
    }
}
