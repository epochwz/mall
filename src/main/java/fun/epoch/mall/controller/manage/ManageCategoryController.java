package fun.epoch.mall.controller.manage;

import fun.epoch.mall.entity.Category;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/manage/category")
public class ManageCategoryController {
    @ResponseBody
    @RequestMapping(value = "add.do", method = POST)
    public ServerResponse<Integer> add(@RequestParam(defaultValue = "0") int parentId, @RequestParam String categoryName) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    public ServerResponse<Category> update(@RequestParam("id") int categoryId, @RequestParam String categoryName) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "enable.do", method = POST)
    public ServerResponse enable(int[] ids) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "disable.do", method = POST)
    public ServerResponse disable(int[] ids) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "list.do")
    public ServerResponse<Category> list(@RequestParam(value = "id", defaultValue = "0") int categoryId) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "list_all.do")
    public ServerResponse<Category> listAll(@RequestParam(value = "id", defaultValue = "0") int categoryId) {
        return null;
    }
}
