package fun.epoch.mall.service;

import fun.epoch.mall.dao.UserMapper;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.MD5Utils;
import fun.epoch.mall.utils.cache.SimpleCache;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static fun.epoch.mall.common.Constant.AccountType.*;
import static fun.epoch.mall.common.Constant.FORGET_TOKEN_PREFIX;
import static fun.epoch.mall.common.Constant.SettingKeys.PASSWORD_SALT;
import static fun.epoch.mall.common.Constant.settings;
import static fun.epoch.mall.common.enhanced.TestHelper.*;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;
import static fun.epoch.mall.utils.response.ResponseCode.UN_AUTHORIZED;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @InjectMocks
    UserService service;

    @Mock
    UserMapper userMapper;

    /**
     * 注册
     * <p>
     * 409  用户名 / 邮箱 / 手机 已存在
     * 200  注册成功，返回新增用户的 id(注册前需要对密码进行 MD5 加密)
     */
    @Test
    public void testRegister_returnConflict_whenUsernameAlreadyExist() {
        when(userMapper.selectCountByUsername(username)).thenReturn(1);
        testIfCodeEqualsConflict(service.register(user));
    }

    @Test
    public void testRegister_returnConflict_whenEmailAlreadyExist() {
        when(userMapper.selectCountByEmail(email)).thenReturn(1);
        testIfCodeEqualsConflict(service.register(user));
    }

    @Test
    public void testRegister_returnConflict_whenMobileAlreadyExist() {
        when(userMapper.selectCountByMobile(mobile)).thenReturn(1);
        testIfCodeEqualsConflict(service.register(user));
    }

    @Test
    public void testRegister_returnSuccess_withUserId_onlyWhenMD5ThePassword_beforeCallMapper() {
        user.setId(null);

        // 模拟：注册前必须对密码进行 MD5 加密，才能成功插入数据库
        when(userMapper.insert(any())).thenAnswer((Answer<Integer>) invocation -> {
            User user = invocation.getArgument(0);
            if (MD5Password.equals(user.getPassword())) {
                user.setId(userId);
                return 1;
            }
            return 0;
        });

        ServerResponse response = testIfCodeEqualsSuccess(service.register(user));

        assertEquals(userId, response.getData());
    }

    /**
     * 账号校验
     * <p>
     * 409  校验失败，账号已存在
     * 200  校验成功 (账号不存在)
     */
    @Test
    public void testAccountVerify_returnConflict_whenAccountAlreadyExist() {
        when(userMapper.selectCountByUsername(username)).thenReturn(1);
        testIfCodeEqualsConflict(service.accountVerify(username, USERNAME));

        when(userMapper.selectCountByEmail(email)).thenReturn(1);
        testIfCodeEqualsConflict(service.accountVerify(email, EMAIL));

        when(userMapper.selectCountByMobile(mobile)).thenReturn(1);
        testIfCodeEqualsConflict(service.accountVerify(mobile, MOBILE));
    }

    @Test
    public void testAccountVerify_returnSuccess_whenAccountNotYetExist() {
        when(userMapper.selectCountByUsername(username)).thenReturn(0);
        testIfCodeEqualsSuccess(service.accountVerify(username, USERNAME));

        when(userMapper.selectCountByEmail(email)).thenReturn(0);
        testIfCodeEqualsSuccess(service.accountVerify(email, EMAIL));

        when(userMapper.selectCountByMobile(mobile)).thenReturn(0);
        testIfCodeEqualsSuccess(service.accountVerify(mobile, MOBILE));
    }

    /**
     * 登录
     * <p>
     * 404  用户名 / 邮箱 / 手机不存在
     * 401  密码错误
     * 200  登录成功 (验证 用户名 / 邮箱 / 手机 三种方式)
     * 200  登录成功，返回用户信息并隐藏密码和密保答案
     */
    @Test
    public void testLogin_returnNotFound_whenAccountNotExist() {
        when(userMapper.selectCountByUsername(username)).thenReturn(0);
        testIfCodeEqualsNotFound(service.login(username, password, USERNAME));

        when(userMapper.selectCountByEmail(email)).thenReturn(0);
        testIfCodeEqualsNotFound(service.login(email, password, EMAIL));

        when(userMapper.selectCountByMobile(mobile)).thenReturn(0);
        testIfCodeEqualsNotFound(service.login(mobile, password, MOBILE));
    }

    @Test
    public void testLogin_returnUnAuthorized_whenPasswordError() {
        when(userMapper.selectCountByUsername(username)).thenReturn(1);
        when(userMapper.selectByUsernameAndPassword(username, MD5Password)).thenReturn(null);
        testIfCodeEquals(UN_AUTHORIZED, service.login(username, password, USERNAME));

        when(userMapper.selectCountByEmail(email)).thenReturn(1);
        when(userMapper.selectByEmailAndPassword(email, MD5Password)).thenReturn(null);
        testIfCodeEquals(UN_AUTHORIZED, service.login(email, password, EMAIL));

        when(userMapper.selectCountByMobile(mobile)).thenReturn(1);
        when(userMapper.selectByMobileAndPassword(mobile, MD5Password)).thenReturn(null);
        testIfCodeEquals(UN_AUTHORIZED, service.login(mobile, password, MOBILE));
    }

    @Test
    public void testLogin_returnSuccess() {
        when(userMapper.selectCountByUsername(username)).thenReturn(1);
        when(userMapper.selectByUsernameAndPassword(username, MD5Password)).thenReturn(user);
        testIfCodeEqualsSuccess(service.login(username, password, USERNAME));

        when(userMapper.selectCountByEmail(email)).thenReturn(1);
        when(userMapper.selectByEmailAndPassword(email, MD5Password)).thenReturn(user);
        testIfCodeEqualsSuccess(service.login(email, password, EMAIL));

        when(userMapper.selectCountByMobile(any())).thenReturn(1);
        when(userMapper.selectByMobileAndPassword(mobile, MD5Password)).thenReturn(user);
        testIfCodeEqualsSuccess(service.login(mobile, password, MOBILE));
    }

    @Test
    public void testLogin_returnSuccess_withUserWithoutPasswordAndAnswer() {
        when(userMapper.selectCountByUsername(username)).thenReturn(1);
        when(userMapper.selectByUsernameAndPassword(username, MD5Password)).thenReturn(user);

        ServerResponse response = testIfCodeEqualsSuccess(service.login(username, password, USERNAME));

        User responseUser = (User) response.getData();
        assertEquals(user, responseUser);

        assertNull(responseUser.getPassword());
        assertNull(responseUser.getAnswer());

        verify(user).setPassword(null);
        verify(user).setAnswer(null);
    }

    /**
     * 查看个人信息
     * <p>
     * 200  查看成功，返回用户信息并隐藏密码和密保答案
     */
    @Test
    public void testGetUserInfo_returnSuccess_withUserWithoutPasswordAndAnswer() {
        when(userMapper.selectByPrimaryKey(userId)).thenReturn(user);

        ServerResponse response = testIfCodeEqualsSuccess(service.getUserInfo(userId));

        User responseUser = (User) response.getData();
        assertEquals(user, responseUser);

        assertNull(responseUser.getPassword());
        assertNull(responseUser.getAnswer());

        verify(user).setPassword(null);
        verify(user).setAnswer(null);
    }

    /**
     * 更新个人信息
     * <p>
     * 409  用户名 / 邮箱 / 手机已存在且不属于当前用户
     * 200  更新成功，返回更新后的用户信息, 并隐藏密码和密保答案
     */
    @Test
    public void testUpdateUserInfo_returnConflict_whenUsernameAlreadyExist() {
        when(userMapper.selectCountByUsernameExceptCurrentUser(userId, username)).thenReturn(1);
        testIfCodeEqualsConflict(service.update(user));
    }

    @Test
    public void testUpdateUserInfo_returnConflict_whenEmailAlreadyExist() {
        when(userMapper.selectCountByEmailExceptCurrentUser(userId, email)).thenReturn(1);
        testIfCodeEqualsConflict(service.update(user));
    }

    @Test
    public void testUpdateUserInfo_returnConflict_whenMobileAlreadyExist() {
        when(userMapper.selectCountByMobileExceptCurrentUser(userId, mobile)).thenReturn(1);
        testIfCodeEqualsConflict(service.update(user));
    }

    @Test
    public void testUpdateUserInfo_returnSuccess_withUpdatedUserWithoutPasswordAndAnswer() {
        when(userMapper.selectCountByUsernameExceptCurrentUser(userId, username)).thenReturn(0);

        // 模拟数据库更新
        User dbUser = User.builder().username("xxx").password("xxx").email("xxx").mobile("xxx").question("xxx").answer("xxx").build();
        when(userMapper.updateSelectiveByPrimaryKey(any())).thenAnswer((Answer<Integer>) invocation -> {
            User user = invocation.getArgument(0);
            if (user.getUsername() != null) dbUser.setUsername(user.getUsername());
            if (user.getEmail() != null) dbUser.setEmail(user.getEmail());
            if (user.getMobile() != null) dbUser.setMobile(user.getMobile());
            if (user.getQuestion() != null) dbUser.setQuestion(user.getQuestion());
            if (user.getAnswer() != null) dbUser.setAnswer(user.getAnswer());
            return 1;
        });

        when(userMapper.selectByPrimaryKey(userId)).thenReturn(dbUser);

        User updateUser = User.builder().id(userId).username(username).password(password).email("\t").question(question).build();
        ServerResponse response = testIfCodeEqualsSuccess(service.update(updateUser));

        User responseUser = (User) response.getData();
        User expectedUser = User.builder().username(username).email("xxx").mobile("xxx").question(question).build();
        assertObjectEquals(expectedUser, responseUser);

        assertNull(responseUser.getPassword());
        assertNull(responseUser.getAnswer());
    }

    /**
     * 重置密码
     * <p>
     * 401  旧密码错误
     * 200  重置成功 (重置前需要对新密码进行 MD5 加密)
     */
    @Test
    public void testResetPassword_returnUnAuthorized_whenOldPassError() {
        when(userMapper.updatePasswordByOldPassword(eq(userId), eq(MD5Password), any())).thenReturn(0);
        testIfCodeEquals(UN_AUTHORIZED, service.resetPassword(userId, password, newPassword));
    }

    @Test
    public void testResetPassword_returnSuccess() {
        when(userMapper.updatePasswordByOldPassword(userId, MD5Password, newMD5Password)).thenReturn(1);
        testIfCodeEqualsSuccess(service.resetPassword(userId, password, newPassword));
    }

    /**
     * 忘记密码 (获取密保问题)
     * <p>
     * 404  用户名不存在
     * 404  密保问题不存在
     * 200  获取成功，返回密保问题
     */
    @Test
    public void testFindQuestion_returnNotFound_whenUsernameNotExist() {
        when(userMapper.selectCountByUsername(username)).thenReturn(0);
        testIfCodeEqualsNotFound(service.findQuestion(username));
    }

    @Test
    public void testFindQuestion_returnNotFound_whenQuestionNotExist() {
        when(userMapper.selectCountByUsername(username)).thenReturn(1);
        when(userMapper.selectQuestionByUsername(username)).thenReturn("");
        testIfCodeEqualsNotFound(service.findQuestion(username));
    }

    @Test
    public void testFindQuestion_returnSuccess_withQuestion() {
        when(userMapper.selectCountByUsername(username)).thenReturn(1);
        when(userMapper.selectQuestionByUsername(username)).thenReturn(question);

        ServerResponse response = testIfCodeEqualsSuccess(service.findQuestion(username));
        assertEquals(question, response.getData());
    }

    /**
     * 提交答案 (获取重置密码的 Token)
     * <p>
     * 401  密保答案错误
     * 200  提交成功，返回重置密码的 Token
     */
    @Test
    public void testCommitAnswer_returnUnAuthorized_whenAnswerError() {
        when(userMapper.selectCountByUsernameAndQuestionAndAnswer(username, question, answer)).thenReturn(0);
        testIfCodeEquals(UN_AUTHORIZED, service.commitAnswer(username, question, answer));
    }

    @Test
    public void testCommitAnswer_returnSuccess_withToken() {
        when(userMapper.selectCountByUsernameAndQuestionAndAnswer(username, question, answer)).thenReturn(1);

        ServerResponse response = testIfCodeEqualsSuccess(service.commitAnswer(username, question, answer));
        assertNotNull(response.getData());
        assertEquals(SimpleCache.get(forgetTokenKey), response.getData());
    }

    /**
     * 重置密码 (通过 Token)
     * <p>
     * 404  Token 已失效
     * 401  Token 不匹配
     * 200  重置密码成功
     */
    @Test
    public void testResetPasswordByToken_returnNotFound_whenTokenNotExist() {
        SimpleCache.clearAll();

        testIfCodeEquals(NOT_FOUND, service.resetPasswordByToken(username, password, "tokenNotExist"));
    }

    @Test
    public void testResetPasswordByToken_returnUnAuthorized_whenTokenNotMatch() {
        SimpleCache.put(forgetTokenKey, forgetToken);

        testIfCodeEquals(UN_AUTHORIZED, service.resetPasswordByToken(username, password, "tokenNotMatch"));
    }

    @Test
    public void testResetPasswordByToken_returnSuccess() {
        SimpleCache.put(forgetTokenKey, forgetToken);

        when(userMapper.updatePasswordByUsername(username, MD5Password)).thenReturn(1);

        testIfCodeEqualsSuccess(service.resetPasswordByToken(username, password, forgetToken));
    }

    // 合法值
    private static final Integer userId = 1000000;
    private static final String username = "epoch";
    private static final String password = "epoch_pass";
    private static final String email = "epoch@gmail.com";
    private static final String mobile = "15626112333";
    private static final String question = "question";
    private static final String answer = "answer";

    private User user = Mockito.spy(User.builder()
            .id(userId)
            .username(username)
            .email(email)
            .mobile(mobile)
            .password(password)
            .question(question)
            .answer(answer)
            .build()
    );

    private static final String MD5Password = MD5Utils.encodeUTF8(password, settings.get(PASSWORD_SALT));

    private static final String newPassword = "epoch_pass_new";
    private static final String newMD5Password = MD5Utils.encodeUTF8(newPassword, settings.get(PASSWORD_SALT));

    private static final String forgetTokenKey = FORGET_TOKEN_PREFIX + username;
    private static final String forgetToken = "b9f655ed-4d71-473f-abff-f989098ff818";
}
