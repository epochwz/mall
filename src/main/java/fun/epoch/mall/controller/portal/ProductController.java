package fun.epoch.mall.controller.portal;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.service.ProductService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @ResponseBody
    @RequestMapping(value = "detail.do")
    public ServerResponse<ProductVo> detail(@RequestParam("id") int productId) {
        return productService.detailOnlyOnSale(productId);
    }

    @ResponseBody
    @RequestMapping(value = "search.do")
    public ServerResponse<PageInfo<ProductVo>> search(
            Integer categoryId,
            String keyword,
            String orderBy,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return productService.searchOnlyOnSale(categoryId, keyword, orderBy, pageNum, pageSize);
    }
}
