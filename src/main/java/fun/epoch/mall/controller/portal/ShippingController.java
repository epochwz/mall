package fun.epoch.mall.controller.portal;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.ShippingService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.controller.common.Checker.checkMobile;
import static fun.epoch.mall.utils.TextUtils.isBlank;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    ShippingService shippingService;

    @ResponseBody
    @RequestMapping(value = "detail.do")
    public ServerResponse<Shipping> detail(HttpSession session, @RequestParam("id") int shippingId) {
        return shippingService.detail(currentUserId(session), shippingId);
    }

    @ResponseBody
    @RequestMapping(value = "list.do")
    public ServerResponse<PageInfo<Shipping>> list(
            HttpSession session,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return shippingService.list(currentUserId(session), pageNum, pageSize);
    }

    @ResponseBody
    @RequestMapping(value = "add.do", method = POST)
    public ServerResponse<Integer> add(HttpSession session, @RequestBody Shipping shipping) {
        if (!checkMobile(shipping.getMobile())
                || isBlank(shipping.getName())
                || isBlank(shipping.getProvince())
                || isBlank(shipping.getCity())
                || isBlank(shipping.getDistrict())
                || isBlank(shipping.getAddress())
        ) {
            return ServerResponse.error("参数不能为空");
        }
        shipping.setUserId(currentUserId(session));
        return shippingService.add(shipping);
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    public ServerResponse<Shipping> update(HttpSession session, @RequestBody Shipping shipping) {
        if (shipping.getId() == null) {
            return ServerResponse.error("收货地址 id 不能为空");
        }
        if (shipping.getMobile() != null && !checkMobile(shipping.getMobile())) {
            return ServerResponse.error("手机号码格式不正确");
        }
        shipping.setUserId(currentUserId(session));
        return shippingService.update(shipping);
    }

    @ResponseBody
    @RequestMapping(value = "delete.do", method = POST)
    public ServerResponse delete(HttpSession session, @RequestParam("id") int shippingId) {
        return shippingService.delete(currentUserId(session), shippingId);
    }

    private int currentUserId(HttpSession session) {
        return ((User) session.getAttribute(CURRENT_USER)).getId();
    }
}
