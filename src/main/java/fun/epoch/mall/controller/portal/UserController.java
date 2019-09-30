package fun.epoch.mall.controller.portal;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.UserService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping(value = "register.do", method = POST)
    public ServerResponse<Integer> register(User user) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "account_verify.do")
    public ServerResponse accountVerify(@RequestParam String account, @RequestParam String type) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "login.do", method = POST)
    public ServerResponse<User> login(HttpSession session, @RequestParam String account, @RequestParam String password, @RequestParam String type) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "logout.do")
    public ServerResponse<User> logout(HttpSession session) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "info.do")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    public ServerResponse<User> update(HttpSession session, User user) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "reset_password.do", method = POST)
    public ServerResponse resetPassword(HttpSession session, @RequestParam String oldPass, @RequestParam String newPass) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "forget_password.do")
    public ServerResponse<String> forgetPassword(@RequestParam String username) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "commit_answer.do", method = POST)
    public ServerResponse<String> commitAnswer(@RequestParam String username, @RequestParam String question, @RequestParam String answer) {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "reset_password_by_token.do", method = POST)
    public ServerResponse resetPasswordByToken(@RequestParam String username, @RequestParam String password, @RequestParam String forgetToken) {
        return null;
    }
}
