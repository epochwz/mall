package fun.epoch.mall.service;

import fun.epoch.mall.dao.UserMapper;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.MD5Utils;
import fun.epoch.mall.utils.cache.SimpleCache;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static fun.epoch.mall.common.Constant.AccountType.*;
import static fun.epoch.mall.common.Constant.FORGET_TOKEN_PREFIX;
import static fun.epoch.mall.common.Constant.SettingKeys.PASSWORD_SALT;
import static fun.epoch.mall.common.Constant.settings;
import static fun.epoch.mall.controller.common.Checker.checkAccount;
import static fun.epoch.mall.utils.TextUtils.isBlank;
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
        ServerResponse validResult = checkAccount(account, type);
        if (validResult.isSuccess()) {
            switch (type) {
                case USERNAME:
                    return userMapper.selectCountByUsername(account) == 0 ? ServerResponse.success() : ServerResponse.error(CONFLICT, "账号已存在");
                case EMAIL:
                    return userMapper.selectCountByEmail(account) == 0 ? ServerResponse.success() : ServerResponse.error(CONFLICT, "邮箱已存在");
                case MOBILE:
                    return userMapper.selectCountByMobile(account) == 0 ? ServerResponse.success() : ServerResponse.error(CONFLICT, "手机已存在");
                default:
                    return ServerResponse.error(NOT_IMPLEMENTED, "暂不支持的账号类型");
            }
        }
        return validResult;
    }

    public ServerResponse<User> login(String account, String password, String type) {
        User user;
        String md5Password = MD5Utils.encodeUTF8(password, settings.get(PASSWORD_SALT));
        switch (type) {
            case USERNAME:
                if (userMapper.selectCountByUsername(account) == 0) {
                    return ServerResponse.error(NOT_FOUND, "账号不存在");
                }
                user = userMapper.selectByUsernameAndPassword(account, md5Password);
                break;
            case EMAIL:
                if (userMapper.selectCountByEmail(account) == 0) {
                    return ServerResponse.error(NOT_FOUND, "邮箱不存在");
                }
                user = userMapper.selectByEmailAndPassword(account, md5Password);
                break;
            case MOBILE:
                if (userMapper.selectCountByMobile(account) == 0) {
                    return ServerResponse.error(NOT_FOUND, "手机不存在");
                }
                user = userMapper.selectByMobileAndPassword(account, md5Password);
                break;
            default:
                return ServerResponse.error(NOT_IMPLEMENTED, "暂不支持的账号类型");
        }

        if (user != null) {
            user.setPassword(null);
            user.setAnswer(null);
            return ServerResponse.success(user);
        }
        return ServerResponse.error(UN_AUTHORIZED, "密码错误");
    }

    public ServerResponse<User> getUserInfo(int userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user != null) {
            user.setPassword(null);
            user.setAnswer(null);
            return ServerResponse.success(user);
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR);
    }

    public ServerResponse<User> update(User user) {
        // 检查账号唯一性
        if (isNotBlank(user.getUsername()) && userMapper.selectCountByUsernameExceptCurrentUser(user.getId(), user.getUsername()) > 0) {
            return ServerResponse.error(CONFLICT, "账号已存在");
        }
        if (isNotBlank(user.getEmail()) && userMapper.selectCountByEmailExceptCurrentUser(user.getId(), user.getEmail()) > 0) {
            return ServerResponse.error(CONFLICT, "邮箱已存在");
        }
        if (isNotBlank(user.getMobile()) && userMapper.selectCountByMobileExceptCurrentUser(user.getId(), user.getMobile()) > 0) {
            return ServerResponse.error(CONFLICT, "手机已存在");
        }

        // 避免更新不必要的字段
        User updateUser = User.builder().id(user.getId()).build();
        if (isNotBlank(user.getUsername())) updateUser.setUsername(user.getUsername());
        if (isNotBlank(user.getEmail())) updateUser.setEmail(user.getEmail());
        if (isNotBlank(user.getMobile())) updateUser.setMobile(user.getMobile());
        if (isNotBlank(user.getQuestion())) updateUser.setQuestion(user.getQuestion());
        if (isNotBlank(user.getAnswer())) updateUser.setAnswer(user.getAnswer());

        if (userMapper.updateSelectiveByPrimaryKey(updateUser) == 1) {
            return getUserInfo(user.getId());
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "更新用户信息失败");
    }

    public ServerResponse resetPassword(int userId, String oldPassword, String newPassword) {
        String oldMD5Password = MD5Utils.encodeUTF8(oldPassword, settings.get(PASSWORD_SALT));
        String newMD5Password = MD5Utils.encodeUTF8(newPassword, settings.get(PASSWORD_SALT));
        if (userMapper.updatePasswordByOldPassword(userId, oldMD5Password, newMD5Password) > 0) {
            return ServerResponse.success();
        }
        return ServerResponse.error(UN_AUTHORIZED, "旧密码错误");
    }

    public ServerResponse<String> findQuestion(String username) {
        if (userMapper.selectCountByUsername(username) == 0) {
            return ServerResponse.error(NOT_FOUND, "账号不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (isBlank(question)) {
            return ServerResponse.error(NOT_FOUND, "未设置密保问题");
        }
        return ServerResponse.success(question);
    }

    public ServerResponse<String> commitAnswer(String username, String question, String answer) {
        if (userMapper.selectCountByUsernameAndQuestionAndAnswer(username, question, answer) == 0) {
            return ServerResponse.error(UN_AUTHORIZED, "密保答案错误");
        }
        String forgetToken = UUID.randomUUID().toString();
        String forgetTokenKey = FORGET_TOKEN_PREFIX + username;
        SimpleCache.put(forgetTokenKey, forgetToken);
        return ServerResponse.success(forgetToken);
    }

    public ServerResponse resetPasswordByToken(String username, String password, String forgetToken) {
        String currentForgetToken = SimpleCache.get(FORGET_TOKEN_PREFIX + username);
        if (isBlank(currentForgetToken)) {
            return ServerResponse.error(NOT_FOUND, "Token 不存在");
        }
        if (!Objects.equals(currentForgetToken, forgetToken)) {
            return ServerResponse.error(UN_AUTHORIZED, "Token 不匹配");
        }
        if (userMapper.updatePasswordByUsername(username, MD5Utils.encodeUTF8(password, settings.get(PASSWORD_SALT))) != 1) {
            return ServerResponse.error(INTERNAL_SERVER_ERROR, "重置密码失败");
        }
        return ServerResponse.success();
    }
}
