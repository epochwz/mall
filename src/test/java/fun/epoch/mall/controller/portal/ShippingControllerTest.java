package fun.epoch.mall.controller.portal;

import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.ShippingService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpSession;

import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsError;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static fun.epoch.mall.mvc.common.Keys.ErrorKeys.idNotExist;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShippingControllerTest {
    @InjectMocks
    ShippingController controller;

    @Mock
    ShippingService service;

    @Mock
    MockHttpSession session;

    @Before
    public void setup() {
        when(session.getAttribute(CURRENT_USER)).thenReturn(User.builder().id(userId).build());
    }

    /**
     * 添加收货地址
     * <p>
     * 400  非法参数：除了 zip 之外其他字段不允许空值
     * 400  非法参数：手机不符合统一校验规则
     * 200  添加成功：调用 service 成功 (调用前设置 userId)
     */
    @Test
    public void testAddShipping_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(blankValues, blankValue -> controller.add(session, mock().name(blankValue).build()));
        testIfCodeEqualsError(blankValues, blankValue -> controller.add(session, mock().mobile(blankValue).build()));
        testIfCodeEqualsError(blankValues, blankValue -> controller.add(session, mock().province(blankValue).build()));
        testIfCodeEqualsError(blankValues, blankValue -> controller.add(session, mock().city(blankValue).build()));
        testIfCodeEqualsError(blankValues, blankValue -> controller.add(session, mock().district(blankValue).build()));
        testIfCodeEqualsError(blankValues, blankValue -> controller.add(session, mock().address(blankValue).build()));

        testIfCodeEqualsError(errorMobiles, errorMobile -> controller.add(session, mock().mobile(errorMobile).build()));
    }

    @Test
    public void testAddShipping_returnSuccess_onlyWhenSetUserId_beforeCallService() {
        when(service.add(any())).thenAnswer((Answer<ServerResponse<Integer>>) invocation -> {
            Shipping shipping = invocation.getArgument(0);
            return userId.equals(shipping.getUserId()) ? ServerResponse.success(shippingId) : ServerResponse.error();
        });

        testIfCodeEqualsSuccess(controller.add(session, mock().build()));
    }

    /**
     * 删除收货地址
     * <p>
     * 200  删除成功
     */
    @Test
    public void testDeleteShipping_returnSuccess_whenCallServiceSuccess() {
        when(service.delete(userId, shippingId)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.delete(session, shippingId));
    }

    /**
     * 修改收货地址
     * <p>
     * 400  非法参数：手机不符合统一校验规则
     * 200  修改成功：返回修改后的收货地址
     */
    @Test
    public void testUpdateShipping_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(errorMobiles, errorMobile -> controller.update(session, mock().mobile(errorMobile).build()));
    }

    @Test
    public void testUpdateShipping_returnSuccess_onlyWhenSetUserId_beforeCallService() {
        when(service.update(any())).thenAnswer((Answer<ServerResponse>) invocation -> {
            Shipping shipping = invocation.getArgument(0);
            return userId.equals(shipping.getUserId()) ? ServerResponse.success() : ServerResponse.error();
        });

        testIfCodeEqualsSuccess(controller.update(session, mock().id(shippingId).userId(Integer.valueOf(idNotExist)).build()));
    }

    /**
     * 查看收货地址
     * <p>
     * 200  查看成功，返回收货地址
     */
    @Test
    public void testGetShippingDetail_returnSuccess_whenCallServiceSuccess() {
        when(service.detail(userId, shippingId)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.detail(session, shippingId));
    }

    /**
     * 查看收货地址列表
     * <p>
     * 200  查看成功，返回收货地址列表
     */
    @Test
    public void testGetShippingList_returnSuccess_whenCallServiceSuccess() {
        when(service.list(userId, 1, 5)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.list(session, 1, 5));
    }

    // 合法值
    private static final Integer userId = 1000000;
    private static final Integer shippingId = 1000000;

    private Shipping.ShippingBuilder mock() {
        return Shipping.builder().name("小明").mobile("15623336666").province("广东省").city("广州市").district("小谷围街道").address("宇宙工业大学");
    }

    // 错误值
    private static final String[] blankValues = {null, "", " ", "\t", "\n"};
    private static final String[] errorMobiles = {"error", "156", "156232534341", "99999999999"};
}
