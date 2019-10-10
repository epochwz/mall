package fun.epoch.mall.service;

import fun.epoch.mall.dao.CategoryMapper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fun.epoch.mall.utils.response.ResponseCode.*;

@Service
public class CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    public ServerResponse<Integer> add(int parentId, String categoryName) {
        if (categoryMapper.selectCountByPrimaryKey(parentId) == 0) {
            return ServerResponse.error(NOT_FOUND, "上级类别不存在");
        }
        if (categoryMapper.selectCountByParentIdAndCategoryName(parentId, categoryName) > 0) {
            return ServerResponse.error(CONFLICT, "商品类别已存在");
        }
        Category category = Category.builder().parentId(parentId).name(categoryName).build();
        if (categoryMapper.insert(category) == 1) {
            return ServerResponse.success(category.getId(), "新增商品类别成功");
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "新增商品类别失败");
    }

    public ServerResponse<Category> update(int categoryId, String categoryName) {
        return null;
    }

    public ServerResponse enable(int[] ids) {
        return null;
    }

    public ServerResponse disable(int[] ids) {
        return null;
    }

    public ServerResponse<Category> list(int categoryId) {
        return null;
    }

    public ServerResponse<Category> listAll(int categoryId) {
        return null;
    }
}
