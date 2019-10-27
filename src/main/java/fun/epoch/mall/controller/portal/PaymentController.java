package fun.epoch.mall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import fun.epoch.mall.service.PaymentService;
import fun.epoch.mall.utils.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static fun.epoch.mall.common.Constant.AlipayCallbackCode.RESPONSE_FAILED;
import static fun.epoch.mall.common.Constant.AlipayCallbackCode.RESPONSE_SUCCESS;

@Slf4j
@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @ResponseBody
    @RequestMapping(value = "alipay/callback.do")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = parseParmas(request.getParameterMap());
        log.info("支付宝回调：sign:{}, trade_status:{}, params:{}", params.get("sign"), params.get("trade_status"), params.toString());

        // 签名验证
        try {
            params.remove("sign_type");
            if (!AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType())) {
                return ServerResponse.error("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            String msg = "支付宝回调签名验证异常：" + e.getMessage();
            log.error(msg, e);
            return ServerResponse.error(msg);
        }

        // 验证回调数据, 避免重复通知
        ServerResponse<Object> callback = paymentService.callback(params);
        if (callback.isError()) {
            log.error(callback.getMsg());
            return RESPONSE_FAILED;
        }
        return RESPONSE_SUCCESS;
    }

    private Map<String, String> parseParmas(Map<String, String[]> requestParams) {
        HashMap<String, String> params = new HashMap<>();
        for (String key : requestParams.keySet()) {
            String[] values = requestParams.get(key);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                builder.append(values[i]);
                if (i != values.length - 1) {
                    builder.append(",");
                }
            }
            params.put(key, builder.toString());
        }
        return params;
    }
}
