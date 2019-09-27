package fun.epoch.mall.controller.portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @ResponseBody
    @RequestMapping(value = "alipay/callback.do")
    public Object alipayCallback(HttpServletRequest request) {
        return null;
    }
}
