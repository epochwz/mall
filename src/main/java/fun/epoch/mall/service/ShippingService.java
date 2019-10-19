package fun.epoch.mall.service;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.dao.ShippingMapper;
import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fun.epoch.mall.utils.response.ResponseCode.INTERNAL_SERVER_ERROR;

@Service
public class ShippingService {
    @Autowired
    ShippingMapper shippingMapper;

    public ServerResponse<Integer> add(Shipping shipping) {
        if (shippingMapper.insert(shipping) > 0) {
            return ServerResponse.success(shipping.getId());
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR);
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

    public ServerResponse<PageInfo<Shipping>> list(int userId, int pageSize, int pageNum) {
        return null;
    }
}
