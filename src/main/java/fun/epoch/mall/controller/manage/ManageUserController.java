package fun.epoch.mall.controller.manage;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.UserService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.Constant.AccountType.USERNAME;
import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.controller.common.Checker.checkPassword;
import static fun.epoch.mall.controller.common.Checker.checkUsername;
import static fun.epoch.mall.utils.response.ResponseCode.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/manage/user")
public class ManageUserController {
    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping(value = "login.do", method = POST)
    public ServerResponse<User> login(HttpSession session, @RequestParam String username, @RequestParam String password) {
        if (checkPassword(password) && checkUsername(username)) {
            ServerResponse<User> loginResult = userService.login(username, password, USERNAME);
            if (loginResult.isSuccess()) {
                User currentUser = loginResult.getData();
                if (currentUser != null && MANAGER == currentUser.getRole()) {
                    session.setAttribute(CURRENT_USER, currentUser);
                    return loginResult;
                }
                return ServerResponse.error(FORBIDDEN, "无访问权限 (不是管理员账号)");
            }
            return loginResult;

        }
        return ServerResponse.error("参数格式不正确");
    }

    @ResponseBody
    @RequestMapping(value = "logout.do")
    public ServerResponse logout(HttpSession session) {
        session.removeAttribute(CURRENT_USER);
        return ServerResponse.success();
    }
}
