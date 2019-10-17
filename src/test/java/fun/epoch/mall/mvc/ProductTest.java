package fun.epoch.mall.mvc;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.mvc.common.Apis;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.utils.response.ResponseCode;
import fun.epoch.mall.vo.ProductVo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.Constant.SaleStatus.OFF_SALE;
import static fun.epoch.mall.common.Constant.SaleStatus.ON_SALE;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.manage.product.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.IdNotExist;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.PRODUCT_SQLS;
import static fun.epoch.mall.mvc.common.Keys.ProductKeys.*;
import static fun.epoch.mall.mvc.common.Keys.Tables.category;
import static fun.epoch.mall.mvc.common.Keys.Tables.product;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ProductTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, MANAGER)
                .database(COMMON_SQLS, PRODUCT_SQLS)
                .launchTable(category, product);
    }

    /**
     * 查看商品详情
     * <p>
     * 200  查看成功，返回商品信息
     * 200  查看成功 (商品已下架)
     * 404  商品不存在
     */
    @Test
    public void testDetail_200_withProductVo() {
        ProductVo actual = productVoFrom(perform(SUCCESS, post(detail).param("id", productId)));
        ProductVo expected = productVoFrom("mock/product/detail.json");
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testDetail_200_whenProductOffSale() {
        perform(SUCCESS, post(detail).param("id", productIdOffSale));
    }

    @Test
    public void testDetail_404_whenProductNotExist() {
        perform(NOT_FOUND, post(detail).param("id", idNotExist));
    }

    /**
     * 搜索商品
     * 200  搜索商品，返回商品列表
     */
    @Test
    public void testSearch_200_withPageInfo() {
        PageInfo<ProductVo> actual = productPageInfoFrom(perform(SUCCESS, post(search)));
        PageInfo<ProductVo> expected = productPageInfoFrom("mock/product/search.json");
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testSearch() {
        assertEqualsSearchedSize(5, post(search));
        assertEqualsSearchedSize(1, post(search).param("productId", productId));
        assertEqualsSearchedSize(4, post(search).param("categoryId", categoryId));
        assertEqualsSearchedSize(2, post(search).param("keyword", keyword));
        assertEqualsSearchedSize(1, post(search)
                .param("categoryId", categoryId)
                .param("keyword", keyword)
        );
    }

    /**
     * 添加商品
     * <p>
     * 200  添加成功，返回新增商品 id
     * 404  商品类别不存在 / 已弃用
     */
    @Test
    public void testAddProduct_200_withProductId() {
        this.database().truncate(product).launch();

        postJson(add, mock().id(null).build(), SUCCESS, productId);
    }

    @Test
    public void testAddProduct_404_whenCategoryNotExist() {
        postJson(add, mock().categoryId(IdNotExist).build(), NOT_FOUND);
    }

    @Test
    public void testAddProduct_404_whenCategoryNotEnable() {
        postJson(add, mock().categoryId(categoryIdNotEnable).build(), NOT_FOUND);
    }

    /**
     * 更新商品
     * <p>
     * 200  更新成功，返回更新后的商品
     * 404  商品类别不存在 / 已弃用
     * 500  商品不存在
     */
    @Test
    public void testUpdateProduct_200_withProduct() {
        postJson(update, mock().name(newProductName).build(), SUCCESS, productId, categoryId, newProductName, price);
    }

    @Test
    public void testUpdateProduct_404_whenCategoryNotExist() {
        postJson(update, mock().categoryId(IdNotExist).build(), NOT_FOUND);
    }

    @Test
    public void testUpdateProduct_404_whenCategoryNotEnable() {
        postJson(update, mock().categoryId(categoryIdNotEnable).build(), NOT_FOUND);
    }

    @Test
    public void testUpdateProduct_400_whenProductNotExist() {
        postJson(update, mock().id(IdNotExist).build(), NOT_FOUND);
    }

    /**
     * 商品上下架
     * <p>
     * 200  商品上下架成功
     * 500  商品上下架失败
     */
    @Test
    public void testShelve_200() {
        shelve(OFF_SALE, SUCCESS, productId, productId2);
        assertProductStatus(OFF_SALE, productId, productId2);

        shelve(ON_SALE, SUCCESS, productId);
        assertProductStatus(ON_SALE, productId);
        assertProductStatus(OFF_SALE, productId2);
    }

    @Test
    public void testShelve_500() {
        shelve(OFF_SALE, INTERNAL_SERVER_ERROR, productId, productId2, idNotExist);
        assertProductStatus(ON_SALE, productId, productId2);
    }

    /**
     * 查看商品详情 (前台)
     * <p>
     * 200  查看成功，返回商品信息
     * 404  商品不存在 / 已下架
     */
    @Test
    public void testPortalDetail_200_withProductVo() {
        ProductVo actual = productVoFrom(perform(SUCCESS, post(Apis.portal.product.detail).param("id", productId)));
        ProductVo expected = productVoFrom("mock/product/detail.json");
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testPortalDetail_404_whenProductNotExist() {
        perform(NOT_FOUND, post(Apis.portal.product.detail).param("id", idNotExist));
    }

    @Test
    public void testPortalDetail_404_whenProductOffSale() {
        perform(NOT_FOUND, post(Apis.portal.product.detail).param("id", productIdOffSale));
    }

    /**
     * 搜索商品
     * 200  搜索商品，返回商品列表
     */
    @Test
    public void testPortalSearch_200_withPageInfo() {
        PageInfo<ProductVo> actual = productPageInfoFrom(perform(SUCCESS, post(Apis.portal.product.search)));
        PageInfo<ProductVo> expected = productPageInfoFrom("mock/product/search_portal.json");
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testPortalSearch() {
        assertEqualsSearchedSize(4, post(Apis.portal.product.search));
        assertEqualsSearchedSize(4, post(Apis.portal.product.search).param("productId", productId));
        assertEqualsSearchedSize(4, post(Apis.portal.product.search).param("categoryId", categoryId));
        assertEqualsSearchedSize(1, post(Apis.portal.product.search).param("keyword", keyword));
        assertEqualsSearchedSize(1, post(Apis.portal.product.search)
                .param("categoryId", categoryId)
                .param("keyword", keyword)
        );
    }

    private ProductVo.ProductVoBuilder mock() {
        return ProductVo.builder().id(Integer.valueOf(productId)).categoryId(Integer.valueOf(categoryId)).name(productName).price(price);
    }

    private void assertEqualsSearchedSize(int expectedSize, MockHttpServletRequestBuilder request) {
        assertEquals(expectedSize, productPageInfoFrom(perform(SUCCESS, request)).getSize());
    }

    private void shelve(int status, ResponseCode code, String... productIds) {
        MockHttpServletRequestBuilder api_shelve = post(shelve)
                .param("ids", productIds)
                .param("status", String.valueOf(status));
        perform(api_shelve, code);
    }

    private void assertProductStatus(int status, String... productIds) {
        Arrays.stream(productIds).forEach(productId -> {
            ResultActions resultActions = perform(post(detail).param("id", productId), SUCCESS);
            int actualStatus = productVoFrom(resultActions).getStatus();
            assertEquals(status, actualStatus);
        });
    }

    private static final String categoryId = "111";
    private static final String keyword = "悲伤";

    private static final Integer categoryIdNotEnable = 333;
}
