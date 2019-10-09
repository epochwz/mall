package fun.epoch.mall.service;

import fun.epoch.mall.entity.Category;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    public ServerResponse<Integer> add(int parentId, String categoryName) {
        return null;
    }

    public ServerResponse<Category> update(int categoryId, String categoryName) {
        return null;
    }
}
