package fun.epoch.mall.service;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.common.Constant;
import fun.epoch.mall.dao.CategoryMapper;
import fun.epoch.mall.dao.ProductMapper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.entity.Product;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static fun.epoch.mall.utils.response.ResponseCode.INTERNAL_SERVER_ERROR;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;

@Service
public class ProductService {
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    ProductMapper productMapper;

    public ServerResponse<ProductVo> detailOnlyOnSale(int productId) {
        return null;
    }

    public ServerResponse<PageInfo<ProductVo>> searchOnlyOnSale(int categoryId, String keyword, int pageNum, int pageSize) {
        return null;
    }

    public ServerResponse<Integer> add(ProductVo productVo) {
        Category category = categoryMapper.selectByPrimaryKey(productVo.getCategoryId());
        if (category == null) {
            return ServerResponse.error(NOT_FOUND, "商品类别不存在");
        }
        if (category.getStatus() == Constant.CategoryStatus.DISABLE) {
            return ServerResponse.error(NOT_FOUND, "商品类别已启用");
        }
        Product product = productVo.to();
        if (productMapper.insert(product) == 1) {
            return ServerResponse.success(product.getId());
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "添加商品失败");
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
