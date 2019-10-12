package fun.epoch.mall.service;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public ServerResponse<ProductVo> update(ProductVo productVo) {
        return null;
    }

    public ServerResponse shelve(int[] ids, int status) {
        return null;
    }

    public ServerResponse<ProductVo> detail(int productId) {
        return null;
    }

    public ServerResponse<PageInfo<ProductVo>> search(Integer productId, Integer categoryId, String keyword, int pageNum, int pageSize) {
        return null;
    }

    public ServerResponse<String> upload(MultipartFile file) {
        return null;
    }
}
