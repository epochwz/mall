package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.CustomMvcTest;
import fun.epoch.mall.vo.CartItemVo;
import fun.epoch.mall.vo.CartVo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.Comparator;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.enhanced.TestHelper.assertObjectEquals;
import static fun.epoch.mall.mvc.common.Apis.portal.cart.count;
import static fun.epoch.mall.mvc.common.Apis.portal.cart.list;
import static fun.epoch.mall.mvc.common.Keys.MockCases.*;
import static fun.epoch.mall.mvc.common.Keys.MockJsons.*;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.CART_SQLS;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.Tables.cart_item;
import static fun.epoch.mall.mvc.common.Keys.Tables.product;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class CartTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init()
                .session(userId, CONSUMER)
                .database(COMMON_SQLS, CART_SQLS)
                .launchTable(cart_item, product);
    }

    /**
     * 查看购物车
     * <p>
     * 200  查看成功，返回购物车详细信息
     * 200  查看成功 (购物车中没有商品)
     * 200  购物车商品处于全选状态
     * 200  购物车商品数量超出限制
     * 200  某商品不存在 / 已下架，则删除该购物车记录
     */
    @Test
    public void testList_200_withCartDetail() {
        assertEqualsDefaultJson(post(list));
    }

    @Test
    public void testList_200_withCartDetail_whenNoProductInCart() {
        this.database().truncate(cart_item).launch();

        CartVo cartVo = cartVoFrom(post(list));

        assertTrue(cartVo.isAllChecked());
        assertEquals(new BigDecimal("0"), cartVo.getCartTotalPrice());
        assertEquals(0, cartVo.getCartItems().size());
    }

    @Test
    public void testList_200_withCartDetail_whenProductAllChecked() {
        this.database().launchCase(CASE_CART_ALL_CHECKED, cart_item);

        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_ALL_CHECKED, post(list));
    }

    @Test
    public void testList_200_withCartDetail_whenProductQuantityLimited() {
        this.database().launchCase(CASE_CART_PRODUCT_QUANTITY_LIMITED, cart_item);

        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_PRODUCT_QUANTITY_LIMITED, post(list));
    }

    @Test
    public void testList_200_withCartDetail_whenProductNotExist() {
        this.database().launchCase(CASE_CART_PRODUCT_NOT_EXIST);

        assertProductCountEquals(17);
        assertEqualsDefaultJson(post(list));
        assertProductCountEquals(11);
    }

    private void assertEqualsDefaultJson(MockHttpServletRequestBuilder request) {
        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_DETAIL, request);
    }

    private void assertEqualsExpectedJson(String expectedJson, MockHttpServletRequestBuilder request) {
        assertEqualsExpectedJson(expectedJson, perform(SUCCESS, request));
    }

    private void assertEqualsExpectedJson(String expectedJson, ResultActions performResult) {
        CartVo expected = cartVoFrom(expectedJson);
        CartVo actual = cartVoFrom(performResult);
        actual.getCartItems().sort(Comparator.comparingInt(CartItemVo::getProductId));
        expected.getCartItems().sort(Comparator.comparingInt(CartItemVo::getProductId));
        assertObjectEquals(expected, actual);
    }

    private CartVo cartVoFrom(MockHttpServletRequestBuilder request) {
        return cartVoFrom(perform(SUCCESS, request));
    }

    private void assertProductCountEquals(int expectedCartProductCount) {
        int actualCartProductCount = dataFrom(perform(SUCCESS, count));
        assertEquals(expectedCartProductCount, actualCartProductCount);
    }
}
