package fun.epoch.mall.service;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    public ServerResponse<ProductVo> detailOnlyOnSale(int productId) {
        return null;
    }

    public ServerResponse<PageInfo<ProductVo>> searchOnlyOnSale(int categoryId, String keyword, int pageNum, int pageSize) {
        return null;
    }

    public ServerResponse<Integer> add(ProductVo productVo) {
        return null;
    }
}
