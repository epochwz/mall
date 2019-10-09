package fun.epoch.mall.controller.manage;

import fun.epoch.mall.entity.Category;
import fun.epoch.mall.service.CategoryService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static fun.epoch.mall.utils.TextUtils.isBlank;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/manage/category")
public class ManageCategoryController {
    @Autowired
    CategoryService categoryService;

    @ResponseBody
    @RequestMapping(value = "add.do", method = POST)
    public ServerResponse<Integer> add(@RequestParam(defaultValue = "0") int parentId, @RequestParam String categoryName) {
        if (isBlank(categoryName)) {
            return ServerResponse.error("商品类别名称不能为空");
        }
        return categoryService.add(parentId, categoryName);
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    public ServerResponse<Category> update(@RequestParam("id") int categoryId, @RequestParam String categoryName) {
        if (isBlank(categoryName)) {
            return ServerResponse.error("商品类别名称不能为空");
        }
        return categoryService.update(categoryId, categoryName);
    }

    @ResponseBody
    @RequestMapping(value = "enable.do", method = POST)
    public ServerResponse enable(int[] ids) {
        return categoryService.enable(ids);
    }

    @ResponseBody
    @RequestMapping(value = "disable.do", method = POST)
    public ServerResponse disable(int[] ids) {
        return categoryService.disable(ids);
    }

    @ResponseBody
    @RequestMapping(value = "list.do")
    public ServerResponse<Category> list(@RequestParam(value = "id", defaultValue = "0") int categoryId) {
        return categoryService.list(categoryId);
    }

    @ResponseBody
    @RequestMapping(value = "list_all.do")
    public ServerResponse<Category> listAll(@RequestParam(value = "id", defaultValue = "0") int categoryId) {
        return categoryService.listAll(categoryId);
    }
}
