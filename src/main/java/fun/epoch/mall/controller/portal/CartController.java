package fun.epoch.mall.controller.portal;

import fun.epoch.mall.service.CartService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @ResponseBody
    @RequestMapping(value = "list.do")
    private ServerResponse<CartVo> list(HttpSession session) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "count.do")
    private ServerResponse<Integer> count(HttpSession session) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "add.do", method = POST)
    private ServerResponse<CartVo> add(HttpSession session, @RequestParam int productId, @RequestParam(defaultValue = "1") int count) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "delete.do", method = POST)
    private ServerResponse<CartVo> delete(HttpSession session, int[] productIds) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    private ServerResponse<CartVo> update(HttpSession session, @RequestParam int productId, @RequestParam int count) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "check.do", method = POST)
    private ServerResponse<CartVo> check(HttpSession session, @RequestParam int productId, @RequestParam boolean checked) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "check_all.do", method = POST)
    private ServerResponse<CartVo> checkAll(HttpSession session, @RequestParam boolean checked) {
        return null;
    }
}
