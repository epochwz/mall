package fun.epoch.mall.mvc;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.vo.ProductVo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.manage.product.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.IdNotExist;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.ProductKeys.*;
import static fun.epoch.mall.mvc.common.Keys.Tables.category;
import static fun.epoch.mall.mvc.common.Keys.Tables.product;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ProductTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, MANAGER)
                .database(COMMON_SQLS)
                .launchTable(category, product);
    }

    /**
     * 查看商品详情
     * <p>
     * 200  查看成功，返回商品信息
     * 404  商品不存在
     */
    @Test
    public void testDetail_200_withProductVo() {
        ProductVo actual = productVoFrom(perform(SUCCESS, post(detail).param("id", productId)));
        ProductVo expected = productVoFrom("mock/product/detail.json");
        assertObjectEquals(expected, actual);
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

        postJson(add, mock().build(), SUCCESS, productId);
    }

    @Test
    public void testAddProduct_404_whenCategoryNotExist() {
        postJson(add, mock().categoryId(IdNotExist).build(), NOT_FOUND);
    }

    private ProductVo.ProductVoBuilder mock() {
        return ProductVo.builder().categoryId(Integer.valueOf(categoryId)).name(productName).price(price);
    }

    private void assertEqualsSearchedSize(int expectedSize, MockHttpServletRequestBuilder request) {
        assertEquals(expectedSize, productPageInfoFrom(perform(SUCCESS, request)).getSize());
    }

    private static final String categoryId = "111";
    private static final String keyword = "悲伤";
}
