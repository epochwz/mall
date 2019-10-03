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
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsError;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
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

    // 合法值
    private static final String username = "epoch";
    private static final String password = "epoch_pass";

    // 错误值
    private static final String[] blankValues = {null, "", " ", "\t", "\n"};
    private static final String[] errorUsernames = {"ThisIsAErrorUsernameLongerThan20Chars", "error@username"};
    private static final String[] errorPasswords = {"short", "ThisIsAErrorPasswordLongerThan32Chars"};
    private static final String[] errorEmails = {"error", "error@", "@error", "error@email"};
    private static final String[] errorMobiles = {"error", "156", "156232534341", "99999999999"};
}
