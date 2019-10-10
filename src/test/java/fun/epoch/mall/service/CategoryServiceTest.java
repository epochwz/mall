package fun.epoch.mall.service;

import fun.epoch.mall.dao.CategoryMapper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static fun.epoch.mall.common.Constant.CategoryStatus.ENABLE;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static fun.epoch.mall.utils.response.ResponseCode.INTERNAL_SERVER_ERROR;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {
    @InjectMocks
    CategoryService service;

    @Mock
    CategoryMapper mapper;

    /**
     * 添加商品类别
     * <p>
     * 404  上级类别不存在
     * 409  上级类别中已存在该商品类别名称
     * 200  添加成功：返回新增类别的 id
     */
    @Test
    public void testAddCategory_returnNotFound_whenSuperCategoryNotExist() {
        when(mapper.selectCountByPrimaryKey(parentIdNotExist)).thenReturn(0);
        testIfCodeEqualsNotFound(service.add(parentIdNotExist, categoryName));
    }

    @Test
    public void testAddCategory_returnConflict_whenCategoryAlreadyExist() {
        when(mapper.selectCountByPrimaryKey(parentId)).thenReturn(1);
        when(mapper.selectCountByParentIdAndCategoryName(parentId, categoryName)).thenReturn(1);

        testIfCodeEqualsConflict(service.add(parentId, categoryName));
    }

    @Test
    public void testAddCategory_returnSuccess_withCategoryId() {
        when(mapper.selectCountByPrimaryKey(parentId)).thenReturn(1);
        when(mapper.selectCountByParentIdAndCategoryName(parentId, categoryName)).thenReturn(0);
        // 数据库模拟：插入数据成功时，返回生成的 ID
        when(mapper.insert(any())).thenAnswer((Answer<Integer>) invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(newCategoryId);
            return 1;
        });

        ServerResponse response = testIfCodeEqualsSuccess(service.add(parentId, categoryName));
        assertEquals(newCategoryId, response.getData());
    }

    /**
     * 更新商品类别
     * <p>
     * 404  商品类别不存在
     * 409  上级类别中已存在该商品类别名称
     * 200  更新成功：返回更新后的商品类别
     */
    @Test
    public void testUpdateCategory_returnNotFound_whenCategoryNotExist() {
        when(mapper.selectByPrimaryKey(categoryIdNotExist)).thenReturn(null);
        testIfCodeEqualsNotFound(service.update(categoryIdNotExist, newCategoryName));
    }

    @Test
    public void testUpdateCategory_returnConflict_whenCategoryAlreadyExist() {
        when(mapper.selectByPrimaryKey(categoryId)).thenReturn(category);
        when(mapper.selectCountByParentIdAndCategoryNameExceptCurrentId(parentId, newCategoryName, categoryId)).thenReturn(1);

        testIfCodeEqualsConflict(service.update(categoryId, newCategoryName));
    }

    @Test
    public void testUpdateCategory_returnSuccess_withUpdatedCategory() {
        when(mapper.selectByPrimaryKey(categoryId)).thenReturn(category);
        when(mapper.selectCountByParentIdAndCategoryNameExceptCurrentId(parentId, newCategoryName, categoryId)).thenReturn(0);

        when(mapper.updateSelectiveByPrimaryKey(any())).thenAnswer((Answer<Integer>) invocation -> {
            Category temp = invocation.getArgument(0);
            if (temp.getName() != null) category.setName(temp.getName());
            return 1;
        });

        ServerResponse<Category> response1 = testIfCodeEqualsSuccess(service.update(categoryId, ""));
        assertEquals(categoryName, response1.getData().getName());
        assertEquals(categoryName, category.getName());

        ServerResponse<Category> response = testIfCodeEqualsSuccess(service.update(categoryId, newCategoryName));
        assertEquals(newCategoryName, response.getData().getName());
        assertEquals(newCategoryName, category.getName());
    }

    /**
     * 启用商品类别
     * <p>
     * 500  启用失败
     * 200  启用成功 (参数为空时)
     * 200  启用成功
     */
    @Test
    public void testEnableCategory_returnInternalServerError() {
        when(mapper.updateStatusByPrimaryKey(any(), eq(ENABLE))).thenReturn(0);
        testIfCodeEquals(INTERNAL_SERVER_ERROR, service.enable(ids));
    }

    @Test
    public void testEnableCategory_returnSuccess_whenIdsIsEmpty() {
        testIfCodeEqualsSuccess(service.enable(null));
        testIfCodeEqualsSuccess(service.enable(new int[0]));
    }

    @Test
    public void testEnableCategory_returnSuccess() {
        when(mapper.updateStatusByPrimaryKey(any(), eq(ENABLE))).thenReturn(1);
        testIfCodeEqualsSuccess(service.enable(ids));
    }

    // 合法值
    private static final int parentId = 1000001;

    private static final int categoryId = 1111111;
    private static final String categoryName = "食品";

    private static final int newCategoryId = 2222222;
    private static final String newCategoryName = "服装";

    private static final int[] ids = {1111111, 2222222, 3333333};

    private Category category = Category.builder().parentId(parentId).id(categoryId).name(categoryName).build();

    // 非法值
    private static final int parentIdNotExist = 999;
    private static final int categoryIdNotExist = 999;
}
