package fun.epoch.mall.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import fun.epoch.mall.dao.ShippingMapper;
import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fun.epoch.mall.utils.TextUtils.isBlank;
import static fun.epoch.mall.utils.response.ResponseCode.*;

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
        ServerResponse<Shipping> response = detail(userId, shippingId);
        if (response.isError()) return response;

        int row = shippingMapper.deleteByPrimaryKey(shippingId);
        return row > 0 ? ServerResponse.success() : ServerResponse.error(INTERNAL_SERVER_ERROR);
    }

    public ServerResponse<Shipping> update(Shipping shipping) {
        ServerResponse<Shipping> response = detail(shipping.getUserId(), shipping.getId());
        if (response.isError()) return response;

        if (isBlank(shipping.getName())) shipping.setName(null);
        if (isBlank(shipping.getMobile())) shipping.setMobile(null);
        if (isBlank(shipping.getProvince())) shipping.setProvince(null);
        if (isBlank(shipping.getCity())) shipping.setCity(null);
        if (isBlank(shipping.getDistrict())) shipping.setDistrict(null);
        if (isBlank(shipping.getAddress())) shipping.setAddress(null);
        if (isBlank(shipping.getZip())) shipping.setZip(null);

        if (shippingMapper.updateSelectiveByPrimaryKey(shipping) > 0) {
            return detail(shipping.getUserId(), shipping.getId());
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "更新收货地址失败");
    }

    public ServerResponse<Shipping> detail(int userId, int shippingId) {
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null) {
            return ServerResponse.error(NOT_FOUND, "收货地址不存在");
        }
        if (shipping.getUserId() != userId) {
            return ServerResponse.error(FORBIDDEN, "无权限 (收货地址不属于当前用户)");
        }
        return ServerResponse.success(shipping);
    }

    public ServerResponse<PageInfo<Shipping>> list(int userId, int pageNum, int pageSize) {
        PageInfo<Shipping> page = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> shippingMapper.selectByUserId(userId)
        );
        return ServerResponse.success(page);
    }
}
