package fun.epoch.mall.service;

import fun.epoch.mall.dao.UserMapper;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.MD5Utils;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fun.epoch.mall.common.Constant.AccountType.*;
import static fun.epoch.mall.common.Constant.SettingKeys.PASSWORD_SALT;
import static fun.epoch.mall.common.Constant.settings;
import static fun.epoch.mall.controller.common.Checker.*;
import static fun.epoch.mall.utils.TextUtils.isNotBlank;
import static fun.epoch.mall.utils.response.ResponseCode.*;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public ServerResponse<Integer> register(User user) {
        if (userMapper.selectCountByUsername(user.getUsername()) == 1) {
            return ServerResponse.error(CONFLICT, "账号已存在");
        }
        if (isNotBlank(user.getEmail()) && userMapper.selectCountByEmail(user.getEmail()) == 1) {
            return ServerResponse.error(CONFLICT, "邮箱已存在");
        }
        if (isNotBlank(user.getMobile()) && userMapper.selectCountByMobile(user.getMobile()) == 1) {
            return ServerResponse.error(CONFLICT, "手机已存在");
        }

        user.setPassword(MD5Utils.encodeUTF8(user.getPassword(), settings.get(PASSWORD_SALT)));
        if (userMapper.insert(user) == 1) {
            return ServerResponse.success(user.getId());
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "注册失败");
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

    public ServerResponse<String> findQuestion(String username) {
        return null;
    }

    public ServerResponse<String> commitAnswer(String username, String question, String answer) {
        return null;
    }

    public ServerResponse resetPasswordByToken(String username, String password, String forgetToken) {
        return null;
    }
}
