package fun.epoch.mall.controller.portal;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.UserService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.Constant.AccountType.*;
import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_IMPLEMENTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @InjectMocks
    UserController controller;

    @Mock
    UserService service;

    @Mock
    MockHttpSession session;

    /**
     * 注册
     * <p>
     * 400  非法参数：昵称不合法
     * 400  非法参数：密码不合法
     * 400  非法参数：邮箱非空时不合法
     * 400  非法参数：手机非空时不合法
     * 200  注册成功：调用 service 成功 (调用 service 之前必须先设置用户角色)
     */
    @Test
    public void testRegister_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(blankValues, blankValue -> controller.register(User.builder().username(blankValue).password(password).build()));
        testIfCodeEqualsError(blankValues, blankValue -> controller.register(User.builder().username(username).password(blankValue).build()));

        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.register(User.builder().username(errorUsername).password(password).build()));
        testIfCodeEqualsError(errorPasswords, errorPassword -> controller.register(User.builder().username(username).password(errorPassword).build()));
        testIfCodeEqualsError(errorEmails, errorEmail -> controller.register(User.builder().username(username).password(password).email(errorEmail).build()));
        testIfCodeEqualsError(errorMobiles, errorMobile -> controller.register(User.builder().username(username).password(password).mobile(errorMobile).build()));
    }

    @Test
    public void testRegister_returnSuccess_onlyWhenSetUserRoleAsConsumer_beforeCallService() {
        when(service.register(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return CONSUMER == user.getRole() ? ServerResponse.success() : ServerResponse.error();
        });

        User spy = Mockito.spy(User.builder().username(username).password(password).build());

        testIfCodeEqualsSuccess(controller.register(spy));

        verify(spy).setRole(CONSUMER);
    }

    /**
     * 账号校验
     * <p>
     * 400  非法参数：账号是空值
     * 400  非法参数：账号类型是空值
     * 400  非法参数：账号类型相对应的账号参数不合法
     * 501  暂不支持：暂不支持的账号类型
     * 200  校验成功：调用 service 成功
     */
    @Test
    public void testAccountVerify_returnError_whenOneOfParamIsEmpty() {
        testIfCodeEqualsError(blankValues, type -> controller.accountVerify(username, type));
        testIfCodeEqualsError(blankValues, type -> controller.accountVerify(email, type));
        testIfCodeEqualsError(blankValues, type -> controller.accountVerify(mobile, type));

        testIfCodeEqualsError(blankValues, errorUsername -> controller.accountVerify(errorUsername, USERNAME));
        testIfCodeEqualsError(blankValues, errorEmail -> controller.accountVerify(errorEmail, EMAIL));
        testIfCodeEqualsError(blankValues, errorMobile -> controller.accountVerify(errorMobile, MOBILE));
    }

    @Test
    public void testAccountVerify_returnError_whenAccountIsInvalid() {
        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.accountVerify(errorUsername, USERNAME));
        testIfCodeEqualsError(errorEmails, errorEmail -> controller.accountVerify(errorEmail, EMAIL));
        testIfCodeEqualsError(errorMobiles, errorMobile -> controller.accountVerify(errorMobile, MOBILE));
    }

    @Test
    public void testAccountVerify_returnNotImplemented_whenAccountTypeIsNotYetSupported() {
        testIfCodeEquals(NOT_IMPLEMENTED, controller.accountVerify(username, notSupportedAccountType));
    }

    @Test
    public void testAccountVerify_returnSuccess_whenCallServiceSuccess() {
        when(service.accountVerify(username, USERNAME)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.accountVerify(username, USERNAME));
    }

    /**
     * 登录
     * <p>
     * 400  非法参数：密码不合法
     * 400  非法参数：账号是空值
     * 400  非法参数：账号类型是空值
     * 400  非法参数：账号类型相对应的账号参数不合法
     * 501  非法参数：暂不支持的账号类型
     * 403  权限不足：账号登录成功，但不是消费者账号
     * 200  登录成功：账号登录成功，并设置 session
     */
    @Test
    public void testLogin_returnError_whenPasswordIsInvalid() {
        testIfCodeEqualsError(blankValues, errorPassword -> controller.login(session, username, errorPassword, USERNAME));
        testIfCodeEqualsError(errorPasswords, errorPassword -> controller.login(session, username, errorPassword, USERNAME));
    }

    @Test
    public void testLogin_returnError_whenOneOfParamIsEmpty() {
        testIfCodeEqualsError(blankValues, type -> controller.login(session, username, password, type));
        testIfCodeEqualsError(blankValues, type -> controller.login(session, email, password, type));
        testIfCodeEqualsError(blankValues, type -> controller.login(session, mobile, password, type));

        testIfCodeEqualsError(blankValues, errorUsername -> controller.login(session, errorUsername, password, USERNAME));
        testIfCodeEqualsError(blankValues, errorEmail -> controller.login(session, errorEmail, password, EMAIL));
        testIfCodeEqualsError(blankValues, errorMobile -> controller.login(session, errorMobile, password, MOBILE));
    }

    @Test
    public void testLogin_returnError_whenAccountIsInvalid() {
        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.login(session, errorUsername, password, USERNAME));
        testIfCodeEqualsError(errorEmails, errorEmail -> controller.login(session, errorEmail, password, EMAIL));
        testIfCodeEqualsError(errorMobiles, errorMobile -> controller.login(session, errorMobile, password, MOBILE));
    }

    @Test
    public void testLogin_returnNotImplemented_whenAccountTypeIsNotYetSupported() {
        testIfCodeEquals(NOT_IMPLEMENTED, controller.login(session, username, password, notSupportedAccountType));
    }

    @Test
    public void testLogin_returnForbidden_whenLoginSuccessButNotConsumer() {
        User loginUser = User.builder().username(username).password(password).role(MANAGER).build();
        when(service.login(username, password, USERNAME)).thenReturn(ServerResponse.success(loginUser));

        testIfCodeEqualsForbidden(controller.login(session, username, password, USERNAME));

        verify(session, never()).setAttribute(eq(CURRENT_USER), any());
    }

    @Test
    public void testLogin_returnSuccess_whenLoginSuccess_andThenSetUserIntoSession() {
        User loginUser = User.builder().username(username).password(password).role(CONSUMER).build();
        when(service.login(username, password, USERNAME)).thenReturn(ServerResponse.success(loginUser));

        testIfCodeEqualsSuccess(controller.login(session, username, password, USERNAME));

        verify(session).setAttribute(CURRENT_USER, loginUser);
    }

    /**
     * 登出 (退出登录)
     * <p>
     * 200  登出成功：删除 session 中的 user
     */
    @Test
    public void testLogout_returnSuccess_andThenDeleteUserFromSession() {
        testIfCodeEqualsSuccess(controller.logout(session));
        verify(session).removeAttribute(CURRENT_USER);
    }

    /**
     * 查看个人信息
     * <p>
     * 200  查看成功：调用 service 成功 (已登录)
     */
    @Test
    public void testGetUserInfo_returnSuccess_whenCallServiceSuccess() {
        when(session.getAttribute(CURRENT_USER)).thenReturn(currentUser);

        when(service.getUserInfo(userId)).thenReturn(ServerResponse.success(currentUser));

        testIfCodeEqualsSuccess(controller.getUserInfo(session));
    }

    /**
     * 更新个人信息
     * <p>
     * 400  非法参数：账号非空时不合法
     * 400  非法参数：邮箱非空时不合法
     * 400  非法参数：手机非空时不合法
     * 403  权限不足：用户 ID 与当前登录用户不一致
     * 200  更新成功：调用 service 成功 (已登录)
     */
    @Test
    public void testUpdateUserInfo_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.update(session, User.builder().id(userId).username(errorUsername).password(password).build()));
        testIfCodeEqualsError(errorEmails, errorEmail -> controller.update(session, User.builder().id(userId).username(username).password(password).email(errorEmail).build()));
        testIfCodeEqualsError(errorMobiles, errorMobile -> controller.update(session, User.builder().id(userId).username(username).password(password).mobile(errorMobile).build()));
    }

    @Test
    public void testUpdateUserInfo_returnForbidden_whenUserIdNotMatch() {
        when(session.getAttribute(CURRENT_USER)).thenReturn(currentUser);

        User updateUser = User.builder().id(userId + 1).build();
        testIfCodeEqualsForbidden(controller.update(session, updateUser));
    }

    @Test
    public void testUpdateUserInfo_returnSuccess_whenCallServiceSuccess() {
        when(session.getAttribute(CURRENT_USER)).thenReturn(currentUser);

        when(service.update(any())).thenReturn(ServerResponse.success());

        User updateUser = User.builder().id(userId).build();
        testIfCodeEqualsSuccess(controller.update(session, updateUser));
    }


    /**
     * 重置密码 (已登录)
     * <p>
     * 400  非法参数：密码不合法
     * 200  重置成功：调用 service 成功 (已登录)
     */
    @Test
    public void testResetPassword_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(blankValues, errorPassword -> controller.resetPassword(session, password, errorPassword));
        testIfCodeEqualsError(blankValues, errorPassword -> controller.resetPassword(session, errorPassword, password));

        testIfCodeEqualsError(errorPasswords, errorPassword -> controller.resetPassword(session, password, errorPassword));
        testIfCodeEqualsError(errorPasswords, errorPassword -> controller.resetPassword(session, errorPassword, password));
    }

    @Test
    public void testResetPassword_returnSuccess_whenCallServiceSuccess() {
        when(session.getAttribute(CURRENT_USER)).thenReturn(currentUser);

        when(service.resetPassword(userId, password, newPassword)).thenReturn(ServerResponse.success());

        testIfCodeEqualsSuccess(controller.resetPassword(session, password, newPassword));
    }

    /**
     * 忘记密码 (获取密保问题)
     * <p>
     * 400  非法参数：账号不合法
     * 200  获取成功：调用 service 成功
     */
    @Test
    public void testForgetPassword_returnError_whenParamIsInvalid() {
        testIfCodeEqualsError(blankValues, errorUsername -> controller.forgetPassword(errorUsername));
        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.forgetPassword(errorUsername));
    }

    @Test
    public void testForgetPassword_returnSuccess_whenCallServiceSuccess() {
        when(service.findQuestion(username)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.forgetPassword(username));
    }

    /**
     * 提交答案 (获取重置密码的 Token)
     * <p>
     * 400  非法参数：其中一个参数不合法
     * 200  获取成功：调用 service 成功
     */
    @Test
    public void testCommitAnswer_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.commitAnswer(errorUsername, question, answer));
        testIfCodeEqualsError(blankValues, errorUsername -> controller.commitAnswer(errorUsername, question, answer));
        testIfCodeEqualsError(blankValues, blankValue -> controller.commitAnswer(username, blankValue, answer));
        testIfCodeEqualsError(blankValues, blankValue -> controller.commitAnswer(username, question, blankValue));
    }

    @Test
    public void testCommitAnswer_returnSuccess_whenCallServiceSuccess() {
        when(service.commitAnswer(username, question, answer)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.commitAnswer(username, question, answer));
    }

    /**
     * 重置密码 (通过 Token)
     * <p>
     * 400  非法参数：其中一个参数不合法
     * 200  重置成功：调用 service 成功
     */
    @Test
    public void testResetPasswordByToken_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.resetPasswordByToken(errorUsername, password, forgetToken));
        testIfCodeEqualsError(errorPasswords, errorPassword -> controller.resetPasswordByToken(username, errorPassword, forgetToken));
        testIfCodeEqualsError(blankValues, blankValue -> controller.resetPasswordByToken(username, password, blankValue));
    }

    @Test
    public void testResetPasswordByToken_returnSuccess_whenCallServiceSuccess() {
        when(service.resetPasswordByToken(username, password, forgetToken)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.resetPasswordByToken(username, password, forgetToken));
    }

    // 合法值
    private static final Integer userId = 1000000;
    private static final String username = "epoch";
    private static final String password = "epoch_pass";
    private static final String email = "epoch@gmail.com";
    private static final String mobile = "15626112333";
    private static final String question = "question";
    private static final String answer = "answer";

    private User currentUser = User.builder().id(userId).build();

    private static final String newPassword = "epoch_pass_new";
    private static final String forgetToken = "b9f655ed-4d71-473f-abff-f989098ff818";

    // 错误值
    private static final String[] blankValues = {null, "", " ", "\t", "\n"};

    private static final String notSupportedAccountType = "notSupportedAccountType";

    private static final String[] errorUsernames = {"ThisIsAErrorUsernameLongerThan20Chars", "error@username"};
    private static final String[] errorPasswords = {"short", "ThisIsAErrorPasswordLongerThan32Chars"};
    private static final String[] errorEmails = {"error", "error@", "@error", "error@email"};
    private static final String[] errorMobiles = {"error", "156", "156232534341", "99999999999"};
}
