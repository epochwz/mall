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

import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    // 合法值
    private static final int parentId = 1000001;
    private static final String categoryName = "食品";
    private static final int newCategoryId = 2222222;

    // 非法值
    private static final int parentIdNotExist = 999;
}
