package fun.epoch.mall.mvc.common;

import fun.epoch.mall.common.enhanced.MvcTestHelper;
import fun.epoch.mall.entity.User;

import static fun.epoch.mall.common.Constant.CURRENT_USER;

public class CustomMvcTest extends MvcTestHelper {
    @Override
    public CustomMvcTest init() {
        super.init();
        return this;
    }

    public CustomMvcTest session(int role) {
        this.session(CURRENT_USER, User.builder().role(role).build());
        return this;
    }

    public CustomMvcTest session(String userId, int role) {
        this.session(CURRENT_USER, User.builder().id(Integer.parseInt(userId)).role(role).build());
        return this;
    }
}
