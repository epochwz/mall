package fun.epoch.mall.controller.manage;

import fun.epoch.mall.service.ProductService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.ProductVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;

import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsError;
import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    /**
     * 上下架商品
     * <p>
     * 400  非法参数：销售状态非空且必须是系统支持的销售状态
     * 200  上下架成功：调用 service 成功
     */
    @Test
    public void testShelve_returnError_whenOneOfParamIsInvalid() {
        testIfCodeEqualsError(controller.shelve(null, statusNotSupported));
    }

    @Test
    public void testShelve_returnSuccess_whenCallServiceSuccess() {
        when(service.shelve(any(), anyInt())).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.shelve(null, status));
    }

    /**
     * 查看商品详情
     * <p>
     * 200  查看成功：调用 service 成功
     */
    @Test
    public void testDetailProduct_returnSuccess_whenCallServiceSuccess() {
        when(service.detail(productId)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.detail(productId));
    }

    /**
     * 搜索商品
     * <p>
     * 200  搜索成功：调用 service 成功
     */
    @Test
    public void testSearchProduct_returnSuccess_whenCallServiceSuccess() {
        when(service.search(productId, categoryId, keyword, pageNum, pageSize)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.search(productId, categoryId, keyword, pageNum, pageSize));
    }

    /**
     * 上传商品图片
     * <p>
     * 200  上传成功：调用 service 成功
     */
    @Test
    public void testUpload_returnSuccess_whenCallServiceSuccess() {
        MultipartFile file = new MockMultipartFile("avatar.jpg", new byte[]{});

        when(service.upload(file)).thenReturn(ServerResponse.success());

        testIfCodeEqualsSuccess(controller.upload(file));
    }

    /**
     * 上传商品图片 (Simditor)
     * <p>
     * 500  上传失败
     * 200  上传成功：调用 service 成功，并封装成 Simditor 响应
     */
    @Test
    public void testUploadBySimditor_returnInternalServerError_andThenAssembleSimditorResponse() {
        MultipartFile file = new MockMultipartFile("avatar.jpg", new byte[]{});

        when(service.upload(file)).thenReturn(ServerResponse.error());

        Map<String, Object> result = controller.uploadBySimditor(file);
        assertEquals(false, result.get("success"));
    }

    @Test
    public void testUploadBySimditor_returnSuccess_andThenAssembleSimditorResponse() {
        String fileName = "avatar.jpg";
        MultipartFile file = new MockMultipartFile(fileName, new byte[]{});

        String filePath = "/path/to/" + fileName;
        when(service.upload(file)).thenReturn(ServerResponse.success(filePath));

        Map<String, Object> result = controller.uploadBySimditor(file);
        assertEquals(true, result.get("success"));
        assertEquals(filePath, result.get("file_path"));
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

    private static final String keyword = "";
    private static final int pageNum = 1;
    private static final int pageSize = 5;

    // 错误值
    private static final String[] blankValues = {null, "", " ", "\t", "\n"};
    private static final Integer statusNotSupported = 999;
}
