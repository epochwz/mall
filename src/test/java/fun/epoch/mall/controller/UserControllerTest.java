package fun.epoch.mall.controller;

import fun.epoch.mall.controller.portal.UserController;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.UserService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.AccountType.*;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_IMPLEMENTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @InjectMocks
    UserController controller;

    @Spy
    UserService service;

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

    // 合法值
    private static final String username = "epoch";
    private static final String password = "epoch_pass";
    private static final String email = "epoch@gmail.com";
    private static final String mobile = "15626112333";

    // 错误值
    private static final String[] blankValues = {null, "", " ", "\t", "\n"};

    private static final String notSupportedAccountType = "notSupportedAccountType";

    private static final String[] errorUsernames = {"ThisIsAErrorUsernameLongerThan20Chars", "error@username"};
    private static final String[] errorPasswords = {"short", "ThisIsAErrorPasswordLongerThan32Chars"};
    private static final String[] errorEmails = {"error", "error@", "@error", "error@email"};
    private static final String[] errorMobiles = {"error", "156", "156232534341", "99999999999"};
}
