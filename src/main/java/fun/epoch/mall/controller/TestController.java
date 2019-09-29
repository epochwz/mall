package fun.epoch.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {
    @ResponseBody
    @RequestMapping("exception_resolver.do")
    public String testExceptionResolver() {
        return "全局异常处理测试：" + 1 / 0;
    }
}
