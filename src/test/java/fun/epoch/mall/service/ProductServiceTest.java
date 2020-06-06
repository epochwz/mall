package fun.epoch.mall.service;

import com.github.pagehelper.PageInfo;
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
import java.util.Collections;
import java.util.List;

import static fun.epoch.mall.common.Constant.CategoryStatus.DISABLE;
import static fun.epoch.mall.common.Constant.CategoryStatus.ENABLE;
import static fun.epoch.mall.common.Constant.SaleStatus.OFF_SALE;
import static fun.epoch.mall.common.Constant.SaleStatus.ON_SALE;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static fun.epoch.mall.utils.response.ResponseCode.INTERNAL_SERVER_ERROR;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(Category.builder().status(ENABLE).build());

        Product dbProduct = Product.builder().mainImage("mainImage").build();
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(dbProduct);
        when(productMapper.updateSelectiveByPrimaryKey(any())).thenAnswer(answerForUpdate(dbProduct));

        ServerResponse<ProductVo> response = testIfCodeEqualsSuccess(service.update(mockUpdate().build()));
        ProductVo updatedProduct = response.getData();
        assertEquals(newProductName, updatedProduct.getName());
        assertEquals(stock * 2, updatedProduct.getStock().intValue());
        assertEquals("detail", updatedProduct.getDetail());
        assertEquals("mainImage", updatedProduct.getMainImage());
        assertArrayEquals(new String[]{"subImage"}, updatedProduct.getSubImages());
    }

    @Test
    public void testUpdateProduct_returnSuccess_withNewProduct_whenOriginMainImageIsNull() {
        when(categoryMapper.selectByPrimaryKey(categoryId)).thenReturn(Category.builder().status(ENABLE).build());

        Product dbProduct = Product.builder().build();
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(dbProduct);
        when(productMapper.updateSelectiveByPrimaryKey(any())).thenAnswer(answerForUpdate(dbProduct));

        ServerResponse<ProductVo> response = testIfCodeEqualsSuccess(service.update(mockUpdate().build()));
        ProductVo updatedProduct = response.getData();
        assertEquals(newProductName, updatedProduct.getName());
        assertEquals(stock * 2, updatedProduct.getStock().intValue());
        assertEquals("detail", updatedProduct.getDetail());
        assertEquals("subImage", updatedProduct.getMainImage());
        assertArrayEquals(new String[]{"subImage"}, updatedProduct.getSubImages());
    }

    /**
     * 商品上下架
     * <p>
     * 500  上下架失败
     * 200  上下架成功
     */
    @Test(expected = org.springframework.transaction.NoTransactionException.class)
    public void testShelve_returnInternalServerError() {
        when(productMapper.updateStatusByPrimaryKey(any(), anyInt())).thenReturn(0);
        testIfCodeEquals(INTERNAL_SERVER_ERROR, service.shelve(ids, ENABLE));
        testIfCodeEquals(INTERNAL_SERVER_ERROR, service.shelve(ids, DISABLE));
    }

    @Test
    public void testShelve_returnSuccess() {
        when(productMapper.updateStatusByPrimaryKey(any(), anyInt())).thenReturn(ids.length);
        testIfCodeEqualsSuccess(service.shelve(ids, ENABLE));
        testIfCodeEqualsSuccess(service.shelve(ids, DISABLE));
        testIfCodeEqualsSuccess(service.shelve(null, status));
        testIfCodeEqualsSuccess(service.shelve(new int[]{}, status));
    }

    /**
     * 查看商品详情
     * <p>
     * 404  商品不存在
     * 200  查看成功，返回商品详情
     */
    @Test
    public void testDetail_returnNotFound_whenProductNotExist() {
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(null);
        testIfCodeEqualsNotFound(service.detail(productId));
    }

    @Test
    public void testDetail_returnSuccess_withProduct() {
        Product product = mock().build().to();
        when(productMapper.selectByPrimaryKey(productId)).thenReturn(product);

        ServerResponse<ProductVo> response = testIfCodeEqualsSuccess(service.detail(productId));
        assertObjectEquals(new ProductVo(product), response.getData());
    }

    /**
     * 搜索商品
     * <p>
     * 200  搜索成功，返回商品列表
     */
    @Test
    public void testListProduct_returnSuccess_withProductList() {
        List<Product> products = Collections.singletonList(mock().build().to());
        when(productMapper.selectSelective(any())).thenReturn(products);

        ServerResponse<PageInfo<ProductVo>> response = testIfCodeEqualsSuccess(service.search(null, null, null, 1, 5));

        assertEquals(1, response.getData().getSize());
        for (int i = 0; i < response.getData().getSize(); i++) {
            assertObjectEquals(products.get(i), response.getData().getList().get(i).to());
        }
    }

    /**
     * 查看商品详情 (排除已下架商品)
     * <p>
     * 404  商品不存在 / 已下架
     * 200  查看成功，返回商品详情
     */
    @Test
    public void testDetailOnlyOnSale_returnNotFound_whenProductNotExistOrOffSale() {
        when(productMapper.selectOnlyOnSaleByPrimaryKey(productId)).thenReturn(null);
        testIfCodeEqualsNotFound(service.detailOnlyOnSale(productId));
    }

    @Test
    public void testDetailOnlyOnSale_returnSuccess_withProduct() {
        Product product = mock().status(OFF_SALE).build().to();
        when(productMapper.selectOnlyOnSaleByPrimaryKey(productId)).thenReturn(product);

        ServerResponse<ProductVo> response = testIfCodeEqualsSuccess(service.detailOnlyOnSale(productId));
        assertObjectEquals(new ProductVo(product), response.getData());
    }

    /**
     * 搜索商品
     * <p>
     * 200  搜索成功，返回商品列表
     */
    @Test
    public void testListProductOnlyOnSale_returnSuccess_withProductList() {
        List<Product> products = Collections.singletonList(mock().build().to());
        when(productMapper.selectSelective(any())).thenAnswer((Answer<List<Product>>) invocation -> {
            Product product = invocation.getArgument(0);
            if (product.getStatus() == ON_SALE) {
                return products;
            }
            return Collections.emptyList();
        });

        ServerResponse<PageInfo<ProductVo>> response = testIfCodeEqualsSuccess(service.searchOnlyOnSale(null, null, null, 1, 5));

        assertEquals(1, response.getData().getSize());
        for (int i = 0; i < response.getData().getSize(); i++) {
            assertObjectEquals(products.get(i), response.getData().getList().get(i).to());
        }
    }

    private Answer<Integer> answerForUpdate(Product product) {
        return invocation -> {
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
        };
    }

    private ProductVo.ProductVoBuilder mockUpdate() {
        return ProductVo.builder()
                .id(productId)
                .categoryId(categoryId)
                .name(newProductName)
                .stock(stock * 2)
                .subtitle("\t")
                .detail("detail")
                .subImages(new String[]{"subImage"});
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

    private static final int[] ids = new int[]{1111111, 2222222, 3333333};
}
