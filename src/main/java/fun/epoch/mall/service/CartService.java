package fun.epoch.mall.service;

import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.CartVo;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    public ServerResponse<CartVo> list(int userId) {
        return null;
    }

    public ServerResponse<Integer> count(int userId) {
        return null;
    }

    public ServerResponse<CartVo> add(int userId, int productId, int count) {
        return null;
    }

    public ServerResponse<CartVo> delete(int userId, int[] productIds) {
        return null;
    }

    public ServerResponse<CartVo> update(int userId, int productId, int count) {
        return null;
    }

    public ServerResponse<CartVo> check(int userId, int productId, boolean checked) {
        return null;
    }

    public ServerResponse<CartVo> checkAll(int userId, boolean checked) {
        return null;
    }
}
