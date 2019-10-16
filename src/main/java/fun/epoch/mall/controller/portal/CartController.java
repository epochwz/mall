package fun.epoch.mall.controller.portal;

import fun.epoch.mall.common.Constant;
import fun.epoch.mall.entity.User;
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
        return cartService.list(currentUserId(session));
    }

    @ResponseBody
    @RequestMapping(value = "count.do")
    private ServerResponse<Integer> count(HttpSession session) {
        return cartService.count(currentUserId(session));
    }

    @ResponseBody
    @RequestMapping(value = "add.do", method = POST)
    private ServerResponse<CartVo> add(HttpSession session, @RequestParam int productId, @RequestParam(defaultValue = "1") int count) {
        return cartService.add(currentUserId(session), productId, count);
    }

    @ResponseBody
    @RequestMapping(value = "delete.do", method = POST)
    private ServerResponse<CartVo> delete(HttpSession session, int[] productIds) {
        return cartService.delete(currentUserId(session), productIds);
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    private ServerResponse<CartVo> update(HttpSession session, @RequestParam int productId, @RequestParam int count) {
        return cartService.update(currentUserId(session), productId, count);
    }

    @ResponseBody
    @RequestMapping(value = "check.do", method = POST)
    private ServerResponse<CartVo> check(HttpSession session, @RequestParam int productId, @RequestParam boolean checked) {
        return cartService.check(currentUserId(session), productId, checked);
    }

    @ResponseBody
    @RequestMapping(value = "check_all.do", method = POST)
    private ServerResponse<CartVo> checkAll(HttpSession session, @RequestParam boolean checked) {
        return cartService.checkAll(currentUserId(session), checked);
    }

    private int currentUserId(HttpSession session) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        return user.getId();
    }
}
