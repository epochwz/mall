package fun.epoch.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("test")
public class TestController {
    @ResponseBody
    @RequestMapping("hello.do")
    public User test() {
        User user = new User();
        user.setUsername("小明");
        return user;
    }
}
