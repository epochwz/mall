package fun.epoch.mall.controller.portal;

import fun.epoch.mall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @ResponseBody
    @RequestMapping(value = "alipay/callback.do")
    public Object alipayCallback(HttpServletRequest request) {
        return null;
    }
}
