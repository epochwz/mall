package fun.epoch.mall.controller.manage;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.service.ProductService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/manage/product")
public class ManageProductController {
    @Autowired
    ProductService productService;

    @ResponseBody
    @RequestMapping(value = "detail.do")
    public ServerResponse<ProductVo> detail(@RequestParam("id") int productId) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "search.do")
    public ServerResponse<PageInfo<ProductVo>> search(
            Integer productId,
            Integer categoryId,
            String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "add.do", method = POST)
    public ServerResponse<Integer> add(@RequestBody ProductVo productVo) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    public ServerResponse<ProductVo> update(@RequestBody ProductVo productVo) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "shelve.do", method = POST)
    public ServerResponse shelve(int[] ids, @RequestParam int status) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "upload.do", method = POST)
    public ServerResponse<String> upload(@RequestParam MultipartFile file) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "upload_by_simditor.do", method = POST)
    public Map<String, Object> uploadBySimditor(@RequestParam MultipartFile file) {
        return null;
    }
}
