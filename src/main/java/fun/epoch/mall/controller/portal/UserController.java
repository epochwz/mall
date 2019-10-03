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

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.controller.common.Checker.checkAccount;
import static fun.epoch.mall.controller.common.Checker.checkAccountsWhenNotEmpty;
import static fun.epoch.mall.utils.response.ResponseCode.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping(value = "register.do", method = POST)
    public ServerResponse<Integer> register(User user) {
        if (checkAccountsWhenNotEmpty(user) && checkAccount(user)) {
            user.setRole(CONSUMER);
            return userService.register(user);
        }
        return ServerResponse.error();
    }

    @ResponseBody
    @RequestMapping(value = "account_verify.do")
    public ServerResponse accountVerify(@RequestParam String account, @RequestParam String type) {
        ServerResponse checkAccount = userService.checkAccount(account, type);
        if (checkAccount.isError()) {
            return checkAccount;
        }
        return userService.accountVerify(account, type);
    }

    @ResponseBody
    @RequestMapping(value = "login.do", method = POST)
    public ServerResponse<User> login(HttpSession session, @RequestParam String account, @RequestParam String password, @RequestParam String type) {
        ServerResponse checkAccount = userService.checkAccount(account, password, type);
        if (checkAccount.isError()) {
            return checkAccount;
        }

        ServerResponse<User> login = userService.login(account, password, type);
        if (login.isError()) {
            return login;
        }

        User currentUser = login.getData();
        if (currentUser != null && CONSUMER == currentUser.getRole()) {
            session.setAttribute(CURRENT_USER, currentUser);
            return login;
        }
        return ServerResponse.error(FORBIDDEN, "无访问权限 (不是消费者账号)");
    }

    @ResponseBody
    @RequestMapping(value = "logout.do")
    public ServerResponse logout(HttpSession session) {
        session.removeAttribute(CURRENT_USER);
        return ServerResponse.success("成功退出登录");
    }

    @ResponseBody
    @RequestMapping(value = "info.do")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return userService.getUserInfo(user.getId());
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
