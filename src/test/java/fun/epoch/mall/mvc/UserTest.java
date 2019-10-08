package fun.epoch.mall.mvc;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.mvc.common.Apis.portal.user.register;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.*;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.AUTO_INCREMENT;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.Tables.user;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.*;
import static fun.epoch.mall.utils.response.ResponseCode.CONFLICT;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;

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

    private User.UserBuilder mockNewUser() {
        return User.builder().username(usernameNotExist).email(emailNotExist).mobile(mobileNotExist).password(passwordNotExist);
    }
}
