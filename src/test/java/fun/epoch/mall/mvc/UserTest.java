package fun.epoch.mall.mvc;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.mvc.common.Apis;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.utils.cache.SimpleCache;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.AccountType.*;
import static fun.epoch.mall.mvc.common.Apis.portal.user.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.*;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.AUTO_INCREMENT;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.Tables.user;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.*;
import static fun.epoch.mall.utils.response.ResponseCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UserTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, CONSUMER)
                .database(COMMON_SQLS)
                .launchTable(user);
    }

    /**
     * 注册
     * <p>
     * 200  注册成功，返回新增用户 id
     * 409  用户名 / 邮箱 / 手机号码 已存在
     */
    @Test
    public void testRegister_200_withUserId() {
        this.database().reset(AUTO_INCREMENT, user).launch();

        postJson(register, mockNewUser().build(), SUCCESS, userId);
    }

    @Test
    public void testRegister_409_whenAccountAlreadyExist() {
        postJson(register, mockNewUser().username(username).build(), CONFLICT);
        postJson(register, mockNewUser().email(email).build(), CONFLICT);
        postJson(register, mockNewUser().mobile(mobile2).build(), CONFLICT);
    }

    /**
     * 账号校验
     * <p>
     * 200  校验成功 (账号不存在)
     * 409  校验失败 (账号已存在)
     */
    @Test
    public void testAccountVerify_200_whenAccountNotYetExist() {
        perform(SUCCESS, post(account_verify)
                .param("account", usernameNotExist)
                .param("type", USERNAME)
        );

        perform(SUCCESS, post(account_verify)
                .param("account", emailNotExist)
                .param("type", EMAIL)
        );

        perform(SUCCESS, post(account_verify)
                .param("account", mobileNotExist)
                .param("type", MOBILE)
        );
    }

    @Test
    public void testAccountVerify_409_whenAccountAlreadyExist() {
        perform(CONFLICT, post(account_verify)
                .param("account", username)
                .param("type", USERNAME)
        );

        perform(CONFLICT, post(account_verify)
                .param("account", email)
                .param("type", EMAIL)
        );

        perform(CONFLICT, post(account_verify)
                .param("account", mobile)
                .param("type", MOBILE)
        );
    }

    /**
     * 登录
     * <p>
     * 200  登录成功，返回用户信息
     * 404  账号不存在
     * 401  密码错误
     * 403  无访问权限 (不是消费者账号)
     */
    @Test
    public void testLogin_200_withUser() {
        MockHttpServletRequestBuilder loginByUsername = post(login)
                .param("account", username)
                .param("password", password)
                .param("type", USERNAME);
        perform(loginByUsername, SUCCESS, userId, username, email, mobile, question);
    }

    @Test
    public void testLogin_404_whenAccountNotExist() {
        perform(NOT_FOUND, post(login)
                .param("account", usernameNotExist)
                .param("password", password)
                .param("type", USERNAME)
        );
    }

    @Test
    public void testLogin_401_whenPasswordError() {
        perform(UN_AUTHORIZED, post(login)
                .param("account", username)
                .param("password", passwordNotExist)
                .param("type", USERNAME)
        );
    }

    @Test
    public void testLogin_403_whenLoginSuccessButNotConsumer() {
        perform(FORBIDDEN, post(login)
                .param("account", admin)
                .param("password", adminPassword)
                .param("type", USERNAME)
        );
    }

    /**
     * 登出
     * <p>
     * 200  登出成功
     */
    @Test
    public void testLogout_200() {
        perform(SUCCESS, post(logout));
    }

    /**
     * 查看个人信息
     * <p>
     * 200  查看个人信息成功
     */
    @Test
    public void testGetUserInfo_200() {
        perform(post(info), SUCCESS, userId, username, email, mobile, question);
    }

    /**
     * 更新个人信息
     * <p>
     * 200  更新个人信息成功
     * 409  用户名 / 邮箱 / 手机号码 已存在
     */
    @Test
    public void testUpdate_200() {
        postJson(update, User.builder().id(UserId).build(), SUCCESS, username, email, mobile, question);

        postJson(update, mockUser().username(usernameNotExist).question("\t").mobile("").build(), SUCCESS, usernameNotExist, email, mobile, question);

        postJson(update, mockUser().email(emailNotExist).mobile(mobileNotExist).build(), SUCCESS, username, emailNotExist, mobileNotExist, question);
    }

    @Test
    public void testUpdate_409_whenAccountAlreadyExist() {
        postJson(update, mockUser().username(username2).build(), CONFLICT);
        postJson(update, mockUser().email(email2).build(), CONFLICT);
        postJson(update, mockUser().mobile(mobile2).build(), CONFLICT);
    }

    /**
     * 重置密码 (已登录状态)
     * <p>
     * 200  重置密码成功
     * 401  旧密码错误
     */
    @Test
    public void testResetPassword_200() {
        perform(SUCCESS, post(login)
                .param("account", username)
                .param("password", password)
                .param("type", USERNAME)
        );

        perform(SUCCESS, post(reset_password)
                .param("oldPass", password)
                .param("newPass", passwordNotExist)
        );

        perform(SUCCESS, post(login)
                .param("account", username)
                .param("password", passwordNotExist)
                .param("type", USERNAME)
        );

        perform(UN_AUTHORIZED, post(login)
                .param("account", username)
                .param("password", password)
                .param("type", USERNAME)
        );
    }

    @Test
    public void testResetPassword_401_whenPasswordError() {
        perform(UN_AUTHORIZED, post(reset_password)
                .param("oldPass", passwordNotExist)
                .param("newPass", password)
        );
    }

    /**
     * 忘记密码 (获取密保问题)
     * <p>
     * 200  获取密保问题成功
     * 404  用户名不存在
     * 404  未设置密保问题
     */
    @Test
    public void testForgetPassword_200() {
        perform(post(forget_password).param("username", username), SUCCESS, question);
    }

    @Test
    public void testForgetPassword_404_whenUsernameNotExist() {
        perform(NOT_FOUND, post(forget_password).param("username", usernameNotExist));
    }

    @Test
    public void testForgetPassword_404_whenQuestionNotExist() {
        perform(NOT_FOUND, post(forget_password).param("username", username2));
    }

    /**
     * 提交答案 (获取重置密码的 Token)
     * <p>
     * 200  获取 Token 成功
     * 401  密保答案错误
     */
    @Test
    public void testCommitAnswer_200() {
        MockHttpServletRequestBuilder commitAnswer = post(commit_answer)
                .param("username", username)
                .param("question", question)
                .param("answer", answer);
        expected(perform(commitAnswer, SUCCESS), SimpleCache.get(forgetTokenKey));
    }

    @Test
    public void testCommitAnswer_401() {
        perform(UN_AUTHORIZED, post(commit_answer)
                .param("username", notExist)
                .param("question", question)
                .param("answer", answer)
        );

        perform(UN_AUTHORIZED, post(commit_answer)
                .param("username", username)
                .param("question", notExist)
                .param("answer", answer)
        );

        perform(UN_AUTHORIZED, post(commit_answer)
                .param("username", username)
                .param("question", question)
                .param("answer", notExist)
        );
    }

    /**
     * 重置密码 (通过 Token)
     * <p>
     * 200  重置密码成功
     * 404  Token 已失效
     * 401  Token 不匹配
     */
    @Test
    public void testResetPasswordByToken_200() {
        SimpleCache.put(forgetTokenKey, forgetToken);
        perform(SUCCESS, post(reset_password_by_token)
                .param("username", username)
                .param("password", password)
                .param("forgetToken", forgetToken)
        );
    }

    @Test
    public void testResetPasswordByToken_404_whenTokenNotExist() {
        SimpleCache.clearAll();
        perform(NOT_FOUND, post(reset_password_by_token)
                .param("username", username)
                .param("password", password)
                .param("forgetToken", forgetToken)
        );
    }

    @Test
    public void testResetPasswordByToken_401_whenTokenNotMatch() {
        SimpleCache.put(forgetTokenKey, UUID.randomUUID().toString());
        perform(UN_AUTHORIZED, post(reset_password_by_token)
                .param("username", username)
                .param("password", password)
                .param("forgetToken", forgetToken)
        );
    }

    /**
     * 后台：登录
     * <p>
     * 200  登录成功，返回用户信息
     * 404  用户名不存在
     * 401  密码错误
     * 403  无访问权限 (不是管理员账号)
     */
    @Test
    public void testManageLogin_200_withUser() {
        MockHttpServletRequestBuilder loginByUsername = post(Apis.manage.user.login)
                .param("username", admin)
                .param("password", adminPassword);
        perform(loginByUsername, SUCCESS, adminId, admin);
    }

    @Test
    public void testManageLogin_404_whenUsernameNotExist() {
        perform(NOT_FOUND, post(Apis.manage.user.login)
                .param("username", notExist)
                .param("password", adminPassword)
        );
    }

    @Test
    public void testManageLogin_401_whenPasswordError() {
        perform(UN_AUTHORIZED, post(Apis.manage.user.login)
                .param("username", admin)
                .param("password", notExist)
        );
    }

    @Test
    public void testManageLogin_403_whenLoginSuccessButNotManage() {
        perform(FORBIDDEN, post(Apis.manage.user.login)
                .param("username", username)
                .param("password", password)
        );
    }

    /**
     * 后台：登出
     * <p>
     * 200  登出成功
     */
    @Test
    public void testManageLogout_200() {
        perform(SUCCESS, post(Apis.manage.user.logout));
    }

    private User.UserBuilder mockNewUser() {
        return User.builder().username(usernameNotExist).email(emailNotExist).mobile(mobileNotExist).password(passwordNotExist);
    }

    private User.UserBuilder mockUser() {
        return User.builder().id(UserId).username(username).email(email).mobile(mobile).password(password);
    }
}
