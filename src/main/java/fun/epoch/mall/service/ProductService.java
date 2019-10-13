package fun.epoch.mall.service;

import com.github.pagehelper.PageHelper;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fun.epoch.mall.common.Constant.SaleStatus.ON_SALE;
import static fun.epoch.mall.utils.TextUtils.isBlank;
import static fun.epoch.mall.utils.TextUtils.isNotBlank;
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
        ServerResponse checkCategory = checkCategory(productVo.getCategoryId());
        if (checkCategory.isError()) return checkCategory;

        Product product = productVo.to();
        if (productMapper.insert(product) == 1) {
            return ServerResponse.success(product.getId());
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "添加商品失败");
    }

    public ServerResponse<ProductVo> update(ProductVo productVo) {
        Product product = productMapper.selectByPrimaryKey(productVo.getId());
        if (product == null) return ServerResponse.error(NOT_FOUND, "商品不存在");

        if (productVo.getCategoryId() != null) {
            ServerResponse checkCategory = checkCategory(productVo.getCategoryId());
            if (checkCategory.isError()) return checkCategory;
        }

        // 避免更新不必要的字段
        if (isBlank(productVo.getName())) productVo.setName(null);
        if (isBlank(productVo.getSubtitle())) productVo.setSubtitle(null);
        if (isBlank(productVo.getDetail())) productVo.setDetail(null);

        Product updatedProduct = productVo.to();
        if (isNotBlank(product.getMainImage()) && isBlank(productVo.getMainImage())) updatedProduct.setMainImage(product.getMainImage());
        if (productVo.getSubImages() == null || productVo.getSubImages().length == 0) updatedProduct.setSubImages(null);

        if (productMapper.updateSelectiveByPrimaryKey(updatedProduct) == 0) {
            return ServerResponse.error(INTERNAL_SERVER_ERROR, "更新商品失败");
        }

        ServerResponse<ProductVo> detail = detail(productVo.getId());
        if (detail != null && detail.isSuccess()) {
            productVo = detail.getData();
        }
        return ServerResponse.success(productVo);
    }

    public ServerResponse shelve(int[] ids, int status) {
        if (ids != null && ids.length > 0) {
            int count = productMapper.updateStatusByPrimaryKey(Arrays.stream(ids).boxed().collect(Collectors.toList()), status);
            if (count != ids.length) {
                String msg = String.format("更新商品销售状态失败：%s --> %s", Arrays.toString(ids), ON_SALE == status ? "上架" : "下架");
                return ServerResponse.error(INTERNAL_SERVER_ERROR, msg);
            }
        }
        return ServerResponse.success();
    }

    public ServerResponse<ProductVo> detail(int productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.error(NOT_FOUND, "商品不存在");
        }
        return ServerResponse.success(new ProductVo(product));
    }

    public ServerResponse<PageInfo<ProductVo>> search(Integer productId, Integer categoryId, String keyword, int pageNum, int pageSize) {
        Product selective = Product.builder().id(productId).categoryId(categoryId).name(keyword).build();
        return getPageInfoServerResponse(pageNum, pageSize, selective);
    }

    public ServerResponse<String> upload(MultipartFile file) {
        return null;
    }

    private ServerResponse checkCategory(int categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category == null) {
            return ServerResponse.error(NOT_FOUND, "商品类别不存在");
        }
        if (category.getStatus() == Constant.CategoryStatus.DISABLE) {
            return ServerResponse.error(NOT_FOUND, "商品类别已弃用");
        }
        return ServerResponse.success();
    }

    private ServerResponse<PageInfo<ProductVo>> getPageInfoServerResponse(int pageNum, int pageSize, Product selective) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectSelective(selective);
        List<ProductVo> productVos = products.stream().map(ProductVo::new).collect(Collectors.toList());
        return ServerResponse.success(new PageInfo<>(productVos));
    }
}
