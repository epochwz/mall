package fun.epoch.mall.controller.manage;

import fun.epoch.mall.service.ProductService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsError;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManageProductControllerTest {
    @InjectMocks
    ManageProductController controller;

    @Mock
    ProductService service;

    /**
     * 添加商品
     * <p>
     * 400  非法参数：商品名称非空
     * 400  非法参数：价格非空且必须大于 0
     * 400  非法参数：库存非空时必须大于等于 0
     * 400  非法参数：销售状态非空时必须是系统支持的销售状态
     * 200  添加成功：调用 service 成功，返回新增商品的 id
     */
    @Test
    public void testAddProduct_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(blankValues, blankProductName -> controller.add(mock().name(blankProductName).build()));

        testIfCodeEqualsError(controller.add(mock().price(null).build()));
        testIfCodeEqualsError(controller.add(mock().price(new BigDecimal("0")).build()));
        testIfCodeEqualsError(controller.add(mock().price(new BigDecimal("-1")).build()));

        testIfCodeEqualsError(controller.add(mock().stock(-1).build()));

        testIfCodeEqualsError(controller.add(mock().status(statusNotSupported).build()));
    }

    @Test
    public void testAddProduct_returnSuccess_whenCallServiceSuccess() {
        when(service.add(any())).thenReturn(ServerResponse.success(productId));

        ServerResponse response = testIfCodeEqualsSuccess(controller.add(mock().id(null).build()));
        assertEquals(productId, response.getData());
    }

    /**
     * 更新商品
     * <p>
     * 400  非法参数：商品 id 非空
     * 400  非法参数：价格非空时必须大于 0
     * 400  非法参数：库存非空时必须大于等于 0
     * 400  非法参数：销售状态非空时必须是系统支持的销售状态
     * 200  添加成功：调用 service 成功
     */
    @Test
    public void testUpdateProduct_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(controller.update(mock().id(null).build()));

        testIfCodeEqualsError(controller.update(mock().price(new BigDecimal("0")).build()));
        testIfCodeEqualsError(controller.update(mock().price(new BigDecimal("-1")).build()));

        testIfCodeEqualsError(controller.update(mock().stock(-1).build()));

        testIfCodeEqualsError(controller.update(mock().status(statusNotSupported).build()));
    }

    @Test
    public void testUpdateProduct_returnSuccess_whenCallServiceSuccess() {
        when(service.update(any())).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.update(mock().build()));
    }

    private ProductVo.ProductVoBuilder mock() {
        return ProductVo.builder().id(productId).categoryId(categoryId).name(name).price(price).stock(stock).status(status);
    }

    // 合法值
    private static final Integer productId = 1111111;
    private static final Integer categoryId = 1111111;
    private static final String name = "瓜子";
    private static final BigDecimal price = new BigDecimal("10.9");
    private static final Integer stock = 10;
    private static final Integer status = 1;

    // 错误值
    private static final String[] blankValues = {null, "", " ", "\t", "\n"};
    private static final Integer statusNotSupported = 999;
}
