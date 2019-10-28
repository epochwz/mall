package fun.epoch.mall.controller.manage;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.UserService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.Constant.AccountType.USERNAME;
import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ManageUserControllerTest {
    @InjectMocks
    private ManageUserController controller;

    @Mock
    private UserService service;

    @Mock
    private MockHttpSession session;

    /**
     * 登录
     * <p>
     * 400  非法参数：其中一个参数不合法
     * 403  权限不足：账号登录成功，但不是管理员账号
     * 200  登录成功：账号登录成功，并设置 session
     */
    @Test
    public void testLogin_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(errorUsernames, errorUsername -> controller.login(session, errorUsername, password));
        testIfCodeEqualsError(errorPasswords, errorPassword -> controller.login(session, username, errorPassword));
    }

    @Test
    public void testLogin_returnForbidden_whenLoginSuccessButNotManager() {
        User loginUser = User.builder().username(username).password(password).role(CONSUMER).build();
        when(service.login(username, password, USERNAME)).thenReturn(ServerResponse.success(loginUser));

        testIfCodeEqualsForbidden(controller.login(session, username, password));

        verify(session, never()).setAttribute(eq(CURRENT_USER), any());
    }

    @Test
    public void testLogin_returnSuccess_whenLoginSuccess_andThenSetUserIntoSession() {
        User loginUser = User.builder().username(username).password(password).role(MANAGER).build();
        when(service.login(username, password, USERNAME)).thenReturn(ServerResponse.success(loginUser));

        testIfCodeEqualsSuccess(controller.login(session, username, password));

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

    // 合法值
    private static final String username = "epoch";
    private static final String password = "epoch_password";

    // 错误值
    private static final String[] errorPasswords = {null, "", " ", "\t", "\n", "short", "ThisIsAErrorPasswordLongerThan32Chars"};
    private static final String[] errorUsernames = {null, "", " ", "\t", "\n", "ThisIsAErrorUsernameLongerThan20Chars", "error@username"};
}
