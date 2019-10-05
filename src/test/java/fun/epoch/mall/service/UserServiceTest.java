package fun.epoch.mall.service;

import fun.epoch.mall.dao.UserMapper;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.MD5Utils;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static fun.epoch.mall.common.Constant.SettingKeys.PASSWORD_SALT;
import static fun.epoch.mall.common.Constant.settings;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsConflict;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
}
