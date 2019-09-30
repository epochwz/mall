package fun.epoch.mall.controller.portal;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.service.OrderService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.QrCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @ResponseBody
    @RequestMapping(value = "detail.do")
    public ServerResponse<OrderVo> detail(HttpSession session, @RequestParam long orderNo) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "search.do")
    public ServerResponse<PageInfo<OrderVo>> search(
            HttpSession session,
            Long orderNo,
            String keyword,
            Integer status,
            Long startTime,
            Long endTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "preview.do")
    public ServerResponse<OrderVo> preview(HttpSession session) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "create.do", method = POST)
    public ServerResponse<OrderVo> create(HttpSession session, @RequestParam int shippingId) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "cancel.do", method = POST)
    public ServerResponse cancel(HttpSession session, @RequestParam long orderNo) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "pay.do", method = POST)
    public ServerResponse<QrCodeVo> pay(
            HttpSession session,
            @RequestParam long orderNo,
            @RequestParam(defaultValue = "1") int paymentType,
            @RequestParam(defaultValue = "1") int paymentPlatform
    ) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "payment_status.do")
    public ServerResponse<Boolean> queryPaymentStatus(HttpSession session, @RequestParam long orderNo) {
        return null;
    }
}
