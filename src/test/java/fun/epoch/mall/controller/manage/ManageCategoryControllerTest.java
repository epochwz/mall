package fun.epoch.mall.controller.manage;

import fun.epoch.mall.service.CategoryService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsError;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManageCategoryControllerTest {
    @InjectMocks
    private ManageCategoryController controller;

    @Mock
    private CategoryService service;

    /**
     * 添加商品类别
     * <p>
     * 400  非法参数：商品类别名称-空值
     * 200  添加成功：调用 service 成功
     */
    @Test
    public void testAddCategory_returnError_whenCategoryNameIsEmpty() {
        testIfCodeEqualsError(blankValues, errorCategoryName -> controller.add(parentId, errorCategoryName));
    }

    @Test
    public void testAddCategory_returnSuccess_whenCallServiceSuccess() {
        when(service.add(parentId, categoryName)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.add(parentId, categoryName));
    }

    /**
     * 更新商品类别
     * <p>
     * 400  非法参数：商品类别名称-空值
     * 200  更新成功：调用 service 成功
     */
    @Test
    public void testUpdateCategory_returnError_whenCategoryNameIsEmpty() {
        testIfCodeEqualsError(blankValues, errorCategoryName -> controller.update(categoryId, errorCategoryName));
    }

    @Test
    public void testUpdateCategory_returnSuccess_whenCallServiceSuccess() {
        when(service.update(categoryId, categoryName)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.update(categoryId, categoryName));
    }

    /**
     * 启用商品类别
     * <p>
     * 200  启用成功：调用 service 成功
     */
    @Test
    public void testEnableCategory_returnSuccess_whenCallServiceSuccess() {
        when(service.enable(any())).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.enable(null));
    }

    /**
     * 禁用商品类别
     * <p>
     * 200  禁用成功：调用 service 成功
     */
    @Test
    public void testDisableCategory_returnSuccess_whenCallServiceSuccess() {
        when(service.disable(any())).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.disable(null));
    }

    // 合法值
    private static final int parentId = 0;
    private static final int categoryId = 1111111;
    private static final String categoryName = "食品";

    // 错误值
    private static final String[] blankValues = {null, "", " ", "\t", "\n"};
}
