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
import static fun.epoch.mall.mvc.common.Apis.portal.cart.*;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static fun.epoch.mall.mvc.common.Keys.MockCases.*;
import static fun.epoch.mall.mvc.common.Keys.MockJsons.*;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.CART_SQLS;
import static fun.epoch.mall.mvc.common.Keys.MockSqls.COMMON_SQLS;
import static fun.epoch.mall.mvc.common.Keys.ProductKeys.*;
import static fun.epoch.mall.mvc.common.Keys.Tables.cart_item;
import static fun.epoch.mall.mvc.common.Keys.Tables.product;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.userId;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
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

    /**
     * 查询购物车商品数量
     * <p>
     * 200  查询成功
     * 200  查询成功 (购物车中没有商品)
     */
    @Test
    public void testCount_200_withCartProductCount() {
        assertProductCountEquals(11);
    }

    @Test
    public void testCount_200_withCartProductCount_whenNoProductInCart() {
        this.database().truncate(cart_item).launch();
        assertProductCountEquals(0);
    }

    /**
     * 添加购物车商品
     * <p>
     * 200  商品不存在
     * 200  商品已下架
     * 200  添加成功：商品已在购物车中
     * 200  添加成功：商品未在购物车中
     */
    @Test
    public void testAdd_200_withCartDetail_whenProductNotExist() {
        assertEqualsDefaultJson(post(add).param("productId", idNotExist));
    }

    @Test
    public void testAdd_200_withCartDetail_whenProductOffSale() {
        assertEqualsDefaultJson(post(add).param("productId", productIdOffSale));
    }

    @Test
    public void testAdd_200_withCartDetail_whenProductAlreadyInCart() {
        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_AFTER_UPDATE, post(add)
                .param("productId", productId3)
        );
    }

    @Test
    public void testAdd_200_withCartDetail_whenProductNotInCartBefore() {
        this.database().launchCase(CASE_CART_ADD_PRODUCT_NOT_IN_CART_BEFORE, cart_item);

        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_AFTER_UPDATE, post(add)
                .param("productId", productId3)
                .param("count", "3")
        );
    }

    /**
     * 删除购物车商品
     * <p>
     * 200  删除成功
     */
    @Test
    public void testDelete_200_withCartDetail() {
        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_AFTER_DELETE,
                post(delete).param("productIds", idNotExist, productId3)
        );
    }

    /**
     * 修改购物车商品数量
     * 200  修改成功 (商品不存在)
     * 200  修改成功
     */
    @Test
    public void testUpdate_200_withCartDetail_whenProductNotExist() {
        assertEqualsDefaultJson(post(update)
                .param("productId", idNotExist)
                .param("count", "1")
        );
    }

    @Test
    public void testUpdate_200_withCartDetail() {
        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_AFTER_UPDATE, post(update)
                .param("productId", productId3)
                .param("count", "3")
        );
    }

    /**
     * 勾选 / 取消勾选
     * <p>
     * 200  勾选 / 取消勾选 成功 (商品不存在)
     * 200  勾选 / 取消勾选 成功
     */
    @Test
    public void testChecked_200_withCartDetail_whenProductNotExist() {
        assertEqualsDefaultJson(post(check)
                .param("productId", idNotExist)
                .param("checked", "true")
        );

        assertEqualsDefaultJson(post(check)
                .param("productId", idNotExist)
                .param("checked", "false")
        );
    }

    @Test
    public void testChecked_200_withCartDetail() {
        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_ALL_CHECKED, post(check)
                .param("productId", productId4)
                .param("checked", "true")
        );
    }

    @Test
    public void testUnChecked_200_withCartDetail() {
        CartVo cartVo = cartVoFrom(perform(SUCCESS, post(check)
                .param("productId", productId3)
                .param("checked", "false")));

        assertFalse(cartVo.isAllChecked());
        assertEquals(new BigDecimal("96.20"), cartVo.getCartTotalPrice());
        assertFalse(cartVo.getCartItems().get(2).isChecked());
    }

    /**
     * 全选 / 全不选
     * <p>
     * 200  全选 / 全不选 成功
     */
    @Test
    public void testCheckedAll_200_withCartDetail() {
        assertEqualsExpectedJson(EXPECTED_JSON_OF_CART_ALL_CHECKED, post(check_all)
                .param("checked", "true")
        );
    }

    @Test
    public void testUnCheckedAll_200_withCartDetail() {
        CartVo cartVo = cartVoFrom(post(check_all).param("checked", "false"));

        assertFalse(cartVo.isAllChecked());
        assertEquals(new BigDecimal("0"), cartVo.getCartTotalPrice());
        assertTrue(cartVo.getCartItems().stream().noneMatch(CartItemVo::isChecked));
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
