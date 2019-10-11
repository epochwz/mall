package fun.epoch.mall.mvc.common;

import com.fasterxml.jackson.core.type.TypeReference;
import fun.epoch.mall.common.enhanced.MvcTestHelper;
import fun.epoch.mall.common.helper.ServerResponseHelper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.test.web.servlet.ResultActions;

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

    /* ******************** 通用 ******************** */
    public static final ServerResponseHelper<Category> categoryHelper = new ServerResponseHelper<>(new TypeReference<ServerResponse<Category>>() {
    });

    public Category categoryFrom(ResultActions resultActions) {
        return categoryHelper.dataOf(content(resultActions));
    }

    public Category categoryFrom(String resource) {
        return categoryHelper.dataFrom(resource);
    }
}
