package fun.epoch.mall.mvc;

import fun.epoch.mall.entity.Category;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.manage.category.list;
import static fun.epoch.mall.mvc.common.Apis.manage.category.list_all;
import static fun.epoch.mall.mvc.common.Keys.CategoryKeys.categoryId;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.Tables.category;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class CategoryTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, MANAGER)
                .database(COMMON_SQLS)
                .launchTable(category);
    }

    /**
     * 查询商品类别列表 (平级)
     * <p>
     * 200  查询成功 (不指定 id, 默认返回全部第一级类别)
     * 200  查询成功 (指定 id, 返回商品类别及其子类别)
     * 200  查询成功 (当 id 不存在时)
     */
    @Test
    public void testList_200_withCategories_whenNoSpecifiedId() {
        assertEqualsExpectedJson("mock/category/list.json", post(list));
    }

    @Test
    public void testList_200_withCategories_whenSpecifiedId() {
        assertEqualsExpectedJson("mock/category/list_id.json", post(list).param("id", categoryId));
    }

    @Test
    public void testList_200_whenCategoryNotExist() {
        perform(SUCCESS, post(list).param("id", idNotExist));
    }

    /**
     * 查询商品类别列表 (递归)
     * <p>
     * 200  查询成功 (不指定 id, 默认返回全部类别)
     * 200  查询成功 (指定 id, 返回商品类别及其递归子类别)
     * 200  查询成功 (当 id 不存在时)
     */
    @Test
    public void testListAll_200_withCategoriesRecursively_whenNoSpecifiedId() {
        assertEqualsExpectedJson("mock/category/list_all.json", post(list_all));
    }

    @Test
    public void testListAll_200_withCategoriesRecursively_whenSpecifiedId() {
        assertEqualsExpectedJson("mock/category/list_all_id.json", post(list_all).param("id", categoryId));
    }

    @Test
    public void testListAll_200_whenCategoryNotExist() {
        perform(SUCCESS, post(list_all).param("id", idNotExist));
    }

    private void assertEqualsExpectedJson(String expectedJson, MockHttpServletRequestBuilder request) {
        Category actual = categoryFrom(perform(SUCCESS, request));
        Category expected = categoryFrom(expectedJson);
        assertObjectEquals(expected, actual);
    }
}
