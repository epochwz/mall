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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static fun.epoch.mall.common.Constant.SaleStatus.OFF_SALE;
import static fun.epoch.mall.common.Constant.SaleStatus.ON_SALE;
import static fun.epoch.mall.utils.TextUtils.isNotBlank;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/manage/product")
public class ManageProductController {
    @Autowired
    ProductService productService;

    @ResponseBody
    @RequestMapping(value = "detail.do")
    public ServerResponse<ProductVo> detail(@RequestParam("id") int productId) {
        return productService.detail(productId);
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
        return productService.search(productId, categoryId, keyword, pageNum, pageSize);
    }

    @ResponseBody
    @RequestMapping(value = "add.do", method = POST)
    public ServerResponse<Integer> add(@RequestBody ProductVo productVo) {
        if (isNotBlank(productVo.getName())
                && productVo.getCategoryId() != null
                && productVo.getPrice() != null
                && productVo.getPrice().compareTo(new BigDecimal("0")) > 0
                && (productVo.getStock() == null || productVo.getStock() >= 0)
                && (productVo.getStatus() == null || productVo.getStatus() == ON_SALE || productVo.getStatus() == OFF_SALE)
        ) {
            return productService.add(productVo);
        }
        return ServerResponse.error("参数不合法");
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    public ServerResponse<ProductVo> update(@RequestBody ProductVo productVo) {
        if (productVo.getId() != null
                && (productVo.getPrice() == null || productVo.getPrice().compareTo(new BigDecimal("0")) > 0)
                && (productVo.getStock() == null || productVo.getStock() >= 0)
                && (productVo.getStatus() == null || productVo.getStatus() == ON_SALE || productVo.getStatus() == OFF_SALE)
        ) {
            return productService.update(productVo);
        }
        return ServerResponse.error("参数不合法");
    }

    @ResponseBody
    @RequestMapping(value = "shelve.do", method = POST)
    public ServerResponse shelve(int[] ids, @RequestParam int status) {
        if (status == ON_SALE || status == OFF_SALE) {
            return productService.shelve(ids, status);
        }
        return ServerResponse.error("暂不支持的商品销售状态");
    }

    @ResponseBody
    @RequestMapping(value = "upload.do", method = POST)
    public ServerResponse<String> upload(@RequestParam MultipartFile file) {
        return productService.upload(file);
    }

    @ResponseBody
    @RequestMapping(value = "upload_by_simditor.do", method = POST)
    public Map<String, Object> uploadBySimditor(@RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>();

        ServerResponse<String> result = productService.upload(file);
        if (result.isError()) {
            map.put("success", false);
            map.put("msg", "图片上传失败");
        } else {
            map.put("success", true);
            map.put("msg", "图片上传成功");
            map.put("file_path", result.getData());
        }

        return map;
    }
}
