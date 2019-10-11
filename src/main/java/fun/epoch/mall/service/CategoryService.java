package fun.epoch.mall.service;

import fun.epoch.mall.dao.CategoryMapper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.utils.TextUtils;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fun.epoch.mall.common.Constant.CategoryStatus.DISABLE;
import static fun.epoch.mall.common.Constant.CategoryStatus.ENABLE;
import static fun.epoch.mall.utils.response.ResponseCode.*;

@Service
public class CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    public ServerResponse<Integer> add(int parentId, String categoryName) {
        if (parentId != 0 && categoryMapper.selectCountByPrimaryKey(parentId) == 0) {
            return ServerResponse.error(NOT_FOUND, "上级类别不存在");
        }
        if (categoryMapper.selectCountByParentIdAndCategoryName(parentId, categoryName) > 0) {
            return ServerResponse.error(CONFLICT, "商品类别已存在");
        }
        Category category = Category.builder().parentId(parentId).name(categoryName).status(ENABLE).build();
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
        return updateStatus(ids, ENABLE);
    }

    public ServerResponse disable(int[] ids) {
        return updateStatus(ids, DISABLE);
    }

    public ServerResponse<Category> list(int categoryId) {
        return list(categoryId, false);
    }

    public ServerResponse<Category> listAll(int categoryId) {
        return list(categoryId, true);
    }

    private ServerResponse updateStatus(int[] ids, int status) {
        if (ids != null && ids.length > 0) {
            List<Integer> list = Arrays.stream(ids).boxed().collect(Collectors.toList());
            if (categoryMapper.updateStatusByPrimaryKey(list, status) != ids.length) {
                String errorMsg = String.format("更新商品类别状态失败：%s --> %s", Arrays.toString(ids), status == ENABLE ? "启用" : "禁用");
                return ServerResponse.error(INTERNAL_SERVER_ERROR, errorMsg);
            }
        }
        return ServerResponse.success();
    }

    private ServerResponse<Category> list(int categoryId, boolean recursive) {
        Category category;
        if (categoryId == 0) {
            category = Category.builder().id(0).parentId(0).name("全部商品类别").build();
        } else {
            category = categoryMapper.selectByPrimaryKey(categoryId);
        }
        if (category != null) {
            fill(category, recursive);
        }
        return ServerResponse.success(category);
    }

    // 递归查询子类别并填充自身
    private void fill(Category category, boolean recursive) {
        List<Category> categories = categoryMapper.selectByParentId(category.getId());
        category.setCategories(categories != null ? categories : new ArrayList<>());
        if (recursive) {
            for (Category c : category.getCategories()) {
                fill(c, true);
            }
        }
    }
}
