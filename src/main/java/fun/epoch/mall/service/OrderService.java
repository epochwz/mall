package fun.epoch.mall.service;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    public ServerResponse<OrderVo> detail(long orderNo) {
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
}
