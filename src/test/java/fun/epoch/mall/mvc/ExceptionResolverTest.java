package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.utils.response.ResponseCode.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ExceptionResolverTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init();
    }

    @Test
    public void test() {
        this
                .session(CONSUMER)
                .printable()
                .perform(post("/test/exception_resolver.do"), INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
    }
}
