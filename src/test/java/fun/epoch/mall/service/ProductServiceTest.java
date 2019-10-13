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
import static org.junit.Assert.assertArrayEquals;
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

    /**
     * 更新商品
     * <p>
     * 404  商品不存在
     * 404  商品类别不存在 / 已弃用
     * 200  更新成功：返回更新后的商品
     */
    @Test
    public void testUpdateProduct_returnNotFound_whenProductNotExist() {
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(Product.builder().build());
        testIfCodeEqualsNotFound(service.update(mock().build()));
    }

    @Test
    public void testUpdateProduct_returnNotFound_whenCategoryNotExist() {
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(Product.builder().build());
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(null);
        testIfCodeEqualsNotFound(service.update(mock().build()));
    }

    @Test
    public void testUpdateProduct_returnNotFound_whenCategoryNotEnable() {
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(Product.builder().build());
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(Category.builder().status(DISABLE).build());
        testIfCodeEqualsNotFound(service.update(mock().build()));
    }

    @Test
    public void testUpdateProduct_returnSuccess_withNewProduct() {
        Product product = Product.builder().mainImage("mainImage").build();
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(product);
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(Category.builder().status(ENABLE).build());
        when(productMapper.updateSelectiveByPrimaryKey(any())).thenAnswer((Answer<Integer>) invocation -> {
            Product updatedProduct = invocation.getArgument(0);
            if (updatedProduct.getCategoryId() != null) product.setCategoryId(updatedProduct.getCategoryId());
            if (updatedProduct.getName() != null) product.setName(updatedProduct.getName());
            if (updatedProduct.getPrice() != null) product.setPrice(updatedProduct.getPrice());
            if (updatedProduct.getStock() != null) product.setStock(updatedProduct.getStock());
            if (updatedProduct.getStatus() != null) product.setStatus(updatedProduct.getStatus());
            if (updatedProduct.getSubtitle() != null) product.setSubtitle(updatedProduct.getSubtitle());
            if (updatedProduct.getDetail() != null) product.setDetail(updatedProduct.getDetail());
            if (updatedProduct.getMainImage() != null) product.setMainImage(updatedProduct.getMainImage());
            if (updatedProduct.getSubImages() != null) product.setSubImages(updatedProduct.getSubImages());
            return 1;
        });

        ProductVo updated = ProductVo.builder()
                .id(productId)
                .categoryId(categoryId)
                .name(newProductName)
                .stock(stock * 2)
                .subtitle("\t")
                .detail("detail")
                .subImages(new String[]{"subImage"})
                .build();
        ServerResponse<ProductVo> response = testIfCodeEqualsSuccess(service.update(updated));
        assertEquals(newProductName, response.getData().getName());
        assertEquals(stock * 2, response.getData().getStock().intValue());
        assertEquals("detail", response.getData().getDetail());
        assertEquals("mainImage", response.getData().getMainImage());
        assertArrayEquals(new String[]{"subImage"}, response.getData().getSubImages());
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

    private static final String newProductName = "新瓜子";
}
