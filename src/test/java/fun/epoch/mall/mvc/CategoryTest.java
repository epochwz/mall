package fun.epoch.mall.mvc;

import fun.epoch.mall.entity.Category;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.manage.category.*;
import static fun.epoch.mall.mvc.common.Keys.CategoryKeys.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.Tables.category;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.*;
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

    /**
     * 添加商品类别
     * <p>
     * 200  添加成功，返回新增商品类别的 id
     * 404  上级类别不存在
     * 409  上级类别中已存在该商品类别名称
     */
    @Test
    public void testAdd_200_withCategoryId() {
        this.database().truncate(category).launch();

        MockHttpServletRequestBuilder addCategory = post(add)
                .param("parentId", parentId)
                .param("categoryName", categoryName);
        perform(addCategory, SUCCESS, categoryId);
    }

    @Test
    public void testAdd_404_whenSuperCategoryNotExist() {
        perform(NOT_FOUND, post(add)
                .param("parentId", idNotExist)
                .param("categoryName", categoryName)
        );
    }

    @Test
    public void testAdd_409_whenCategoryAlreadyExist() {
        perform(CONFLICT, post(add)
                .param("parentId", parentId)
                .param("categoryName", categoryName)
        );
    }

    /**
     * 更新商品类别
     * <p>
     * 200  更新成功，返回更新后的商品类别信息
     * 404  商品类别不存在
     * 409  上级类别中已存在该商品类别名称
     */
    @Test
    public void testUpdate_200_withUpdatedCategory() {
        MockHttpServletRequestBuilder updateCategory = post(update)
                .param("id", categoryId)
                .param("categoryName", newCategoryName);
        perform(updateCategory, SUCCESS, categoryId, newCategoryName);
    }

    @Test
    public void testUpdate_404_whenCategoryNotExist() {
        perform(NOT_FOUND, post(update)
                .param("id", idNotExist)
                .param("categoryName", newCategoryName)
        );
    }

    @Test
    public void testUpdate_409_whenCategoryAlreadyExist() {
        perform(CONFLICT, post(update)
                .param("id", categoryId)
                .param("categoryName", categoryName2)
        );
    }

    /**
     * 启用商品类别
     * <p>
     * 200  启用成功
     * 500  启用失败
     */
    @Test
    public void testEnable_200() {
        perform(SUCCESS, post(enable).param("ids", categoryId, categoryId2));
    }

    @Test
    public void testEnable_500_whenOneOfError() {
        perform(INTERNAL_SERVER_ERROR, post(enable)
                .param("ids", categoryId, categoryId2, idNotExist)
        );
    }

    /**
     * 禁用商品类别
     * <p>
     * 200  禁用成功
     * 500  禁用失败
     */
    @Test
    public void testDisable_200() {
        perform(SUCCESS, post(disable).param("ids", categoryId, categoryId2));
    }

    @Test
    public void testDisable_400_whenOneOfError() {
        perform(INTERNAL_SERVER_ERROR, post(disable)
                .param("ids", categoryId, categoryId2, idNotExist)
        );
    }

    private void assertEqualsExpectedJson(String expectedJson, MockHttpServletRequestBuilder request) {
        Category actual = categoryFrom(perform(SUCCESS, request));
        Category expected = categoryFrom(expectedJson);
        assertObjectEquals(expected, actual);
    }
}
