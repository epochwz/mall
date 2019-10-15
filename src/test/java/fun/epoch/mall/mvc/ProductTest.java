package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.Tables.category;
import static fun.epoch.mall.mvc.common.Keys.Tables.product;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;

public class ProductTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, MANAGER)
                .database(COMMON_SQLS)
                .launchTable(category, product);
    }
}
