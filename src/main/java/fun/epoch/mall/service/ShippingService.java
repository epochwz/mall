package fun.epoch.mall.service;

import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {
    public ServerResponse<Integer> add(Shipping shipping) {
        return null;
    }

    public ServerResponse delete(int userId, int shippingId) {
        return null;
    }

    public ServerResponse<Shipping> update(Shipping shipping) {
        return null;
    }

    public ServerResponse<Shipping> detail(int userId, int shippingId) {
        return null;
    }
}
