package fun.epoch.mall.mvc;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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

    private User.UserBuilder mockNewUser() {
        return User.builder().username(usernameNotExist).email(emailNotExist).mobile(mobileNotExist).password(passwordNotExist);
    }
}
