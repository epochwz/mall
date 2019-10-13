package fun.epoch.mall.service;

import fun.epoch.mall.dao.CategoryMapper;
import fun.epoch.mall.dao.ProductMapper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.entity.Product;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;

import static fun.epoch.mall.common.Constant.CategoryStatus.DISABLE;
import static fun.epoch.mall.common.Constant.CategoryStatus.ENABLE;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsNotFound;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {
    @InjectMocks
    ProductService service;

    @Mock
    ProductMapper productMapper;

    @Mock
    CategoryMapper categoryMapper;

    /**
     * 添加商品
     * <p>
     * 404  商品类别不存在 / 已弃用
     * 200  添加成功：返回新增商品的 id
     */
    @Test
    public void testAddProduct_returnNotFound_whenCategoryNotExist() {
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(null);
        testIfCodeEqualsNotFound(service.add(mock().build()));
    }

    @Test
    public void testAddProduct_returnNotFound_whenCategoryNotEnable() {
        Category disableCategory = Category.builder().status(DISABLE).build();
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(disableCategory);
        testIfCodeEqualsNotFound(service.add(mock().build()));
    }

    @Test
    public void testAddProduct_returnSuccess_withProductId() {
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(Category.builder().status(ENABLE).build());
        when(productMapper.insert(any())).thenAnswer((Answer<Integer>) invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(productId);
            return 1;
        });

        ServerResponse<Integer> response = testIfCodeEqualsSuccess(service.add(mock().build()));
        assertEquals(productId, response.getData());
    }

    private ProductVo.ProductVoBuilder mock() {
        return ProductVo.builder().id(productId).categoryId(categoryId).name(name).price(price).stock(stock).status(status);
    }

    // 合法值
    private static final Integer productId = 1111111;
    private static final Integer categoryId = 1111111;
    private static final String name = "瓜子";
    private static final BigDecimal price = new BigDecimal("10.9");
    private static final Integer stock = 10;
    private static final Integer status = 1;
}
