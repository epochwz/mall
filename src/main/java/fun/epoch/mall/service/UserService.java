package fun.epoch.mall.service;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.stereotype.Service;

import static fun.epoch.mall.common.Constant.AccountType.*;
import static fun.epoch.mall.controller.common.Checker.*;
import static fun.epoch.mall.utils.TextUtils.isNotBlank;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_IMPLEMENTED;

@Service
public class UserService {
    public ServerResponse<Integer> register(User user) {
        return null;
    }

    public ServerResponse accountVerify(String account, String type) {
        return null;
    }

    public ServerResponse<User> login(String username, String password, String type) {
        return null;
    }

    public ServerResponse checkAccount(String account, String password, String type) {
        if (!checkPassword(password)) {
            return ServerResponse.error("密码格式不正确");
        }
        return checkAccount(account, type);
    }

    public ServerResponse checkAccount(String account, String type) {
        if (isNotBlank(type) && isNotBlank(account)) {
            switch (type) {
                case USERNAME:
                    return checkUsername(account) ? ServerResponse.success() : ServerResponse.error("账号格式不正确");
                case EMAIL:
                    return checkEmail(account) ? ServerResponse.success() : ServerResponse.error("邮箱格式不正确");
                case MOBILE:
                    return checkMobile(account) ? ServerResponse.success() : ServerResponse.error("手机格式不正确");
                default:
                    return ServerResponse.error(NOT_IMPLEMENTED, "暂不支持的账号类型");
            }
        }
        return ServerResponse.error("参数不能为空");
    }

    public ServerResponse<User> getUserInfo(int userId) {
        return null;
    }

    public ServerResponse<User> update(User user) {
        return null;
    }

    public ServerResponse resetPassword(int userId, String oldPassword, String newPassword) {
        return null;
    }
}
