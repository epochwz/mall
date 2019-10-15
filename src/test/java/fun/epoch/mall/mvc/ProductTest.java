package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.vo.ProductVo;
import org.junit.Before;
import org.junit.Test;

import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.manage.product.detail;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.ProductKeys.productId;
import static fun.epoch.mall.mvc.common.Keys.Tables.category;
import static fun.epoch.mall.mvc.common.Keys.Tables.product;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.NOT_FOUND;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ProductTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, MANAGER)
                .database(COMMON_SQLS)
                .launchTable(category, product);
    }

    /**
     * 查看商品详情
     * <p>
     * 200  查看成功，返回商品信息
     * 404  商品不存在
     */
    @Test
    public void testDetail_200_withProductVo() {
        ProductVo actual = productVoFrom(perform(SUCCESS, post(detail).param("id", productId)));
        ProductVo expected = productVoFrom("mock/product/detail.json");
        assertObjectEquals(expected, actual);
    }

    @Test
    public void testDetail_404_whenProductNotExist() {
        perform(NOT_FOUND, post(detail).param("id", idNotExist));
    }

}
