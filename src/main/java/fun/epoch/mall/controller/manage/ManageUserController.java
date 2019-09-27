package fun.epoch.mall.controller.manage;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/manage/user")
public class ManageUserController {
    @ResponseBody
    @RequestMapping(value = "login.do", method = POST)
    public ServerResponse<User> login(HttpSession session, @RequestParam String username, @RequestParam String password) {
        return null;
    }

    @RequestMapping(value = "logout.do")
    public ServerResponse logout(HttpSession session) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "reset_password.do", method = POST)
    public ServerResponse resetPassword(HttpSession session, @RequestParam String oldPass, @RequestParam String newPass) {
        return null;
    }
}
