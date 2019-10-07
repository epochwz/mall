package fun.epoch.mall.controller.portal;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.UserService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.controller.common.Checker.*;
import static fun.epoch.mall.utils.TextUtils.isBlank;
import static fun.epoch.mall.utils.response.ResponseCode.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping(value = "register.do", method = POST)
    public ServerResponse<Integer> register(@RequestBody User user) {
        if (checkAccountsWhenNotEmpty(user) && checkAccount(user)) {
            user.setRole(CONSUMER);
            return userService.register(user);
        }
        return ServerResponse.error();
    }

    @ResponseBody
    @RequestMapping(value = "account_verify.do")
    public ServerResponse accountVerify(@RequestParam String account, @RequestParam String type) {
        ServerResponse checkAccount = checkAccount(account, type);
        if (checkAccount.isError()) {
            return checkAccount;
        }
        return userService.accountVerify(account, type);
    }

    @ResponseBody
    @RequestMapping(value = "login.do", method = POST)
    public ServerResponse<User> login(HttpSession session, @RequestParam String account, @RequestParam String password, @RequestParam String type) {
        ServerResponse checkAccount = checkAccount(account, password, type);
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
        return userService.getUserInfo(currentUserId(session));
    }

    @ResponseBody
    @RequestMapping(value = "update.do", method = POST)
    public ServerResponse<User> update(HttpSession session, @RequestBody User user) {
        if (!checkAccountsWhenNotEmpty(user)) {
            return ServerResponse.error("账号相关参数格式不正确");
        }

        if (!Objects.equals(currentUserId(session), user.getId())) {
            return ServerResponse.error(FORBIDDEN, "用户 ID 与当前登录用户不一致");
        }
        return userService.update(user);
    }

    @ResponseBody
    @RequestMapping(value = "reset_password.do", method = POST)
    public ServerResponse resetPassword(HttpSession session, @RequestParam String oldPass, @RequestParam String newPass) {
        if (checkPassword(oldPass) && checkPassword(newPass)) {
            return userService.resetPassword(currentUserId(session), oldPass, newPass);
        }
        return ServerResponse.error("密码格式错误");
    }

    @ResponseBody
    @RequestMapping(value = "forget_password.do")
    public ServerResponse<String> forgetPassword(@RequestParam String username) {
        if (!checkUsername(username)) {
            return ServerResponse.error("账号格式不正确");
        }
        return userService.findQuestion(username);
    }

    @ResponseBody
    @RequestMapping(value = "commit_answer.do", method = POST)
    public ServerResponse<String> commitAnswer(@RequestParam String username, @RequestParam String question, @RequestParam String answer) {
        if (!checkUsername(username) || isBlank(question) || isBlank(answer)) {
            return ServerResponse.error("参数格式不正确");
        }
        return userService.commitAnswer(username, question, answer);
    }

    @ResponseBody
    @RequestMapping(value = "reset_password_by_token.do", method = POST)
    public ServerResponse resetPasswordByToken(@RequestParam String username, @RequestParam String password, @RequestParam String forgetToken) {
        if (!checkUsername(username) || !checkPassword(password) || isBlank(forgetToken)) {
            return ServerResponse.error("参数格式不正确");
        }
        return userService.resetPasswordByToken(username, password, forgetToken);
    }

    private Integer currentUserId(HttpSession session) {
        return ((User) session.getAttribute(CURRENT_USER)).getId();
    }
}
