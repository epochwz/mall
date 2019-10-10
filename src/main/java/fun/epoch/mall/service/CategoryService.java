package fun.epoch.mall.service;

import fun.epoch.mall.dao.CategoryMapper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.utils.TextUtils;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fun.epoch.mall.common.Constant.CategoryStatus.ENABLE;
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
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category == null) {
            return ServerResponse.error(NOT_FOUND, "商品类别不存在");
        }
        if (categoryMapper.selectCountByParentIdAndCategoryNameExceptCurrentId(category.getParentId(), categoryName, categoryId) > 0) {
            return ServerResponse.error(CONFLICT, "商品类别已存在");
        }
        if (TextUtils.isNotBlank(categoryName)) category.setName(categoryName);
        if (categoryMapper.updateSelectiveByPrimaryKey(category) == 1) {
            return ServerResponse.success(category, "更新商品类别成功");
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "更新商品类别失败");
    }

    public ServerResponse enable(int[] ids) {
        if (ids != null && ids.length > 0) {
            List<Integer> list = Arrays.stream(ids).boxed().collect(Collectors.toList());
            if (categoryMapper.updateStatusByPrimaryKey(list, ENABLE) == 0) {
                return ServerResponse.error(INTERNAL_SERVER_ERROR, "启用商品类别失败");
            }
        }
        return ServerResponse.success();
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
