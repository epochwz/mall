package fun.epoch.mall.controller.portal;

import fun.epoch.mall.service.ProductService;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static fun.epoch.mall.common.enhanced.TestHelper.testIfCodeEqualsSuccess;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest {
    @InjectMocks
    ProductController controller;

    @Mock
    ProductService service;

    @Test
    public void testDetailProduct_returnSuccess_whenCallServiceSuccess() {
        when(service.detailOnlyOnSale(productId)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.detail(productId));
    }

    @Test
    public void testSearchProduct_returnSuccess_whenCallServiceSuccess() {
        when(service.searchOnlyOnSale(categoryId, keyword, null, pageNum, pageSize)).thenReturn(ServerResponse.success());
        testIfCodeEqualsSuccess(controller.search(categoryId, keyword, null, pageNum, pageSize));
    }

    // 合法值
    private static final int productId = 1111111;
    private static final int categoryId = 1111111;
    private static final String keyword = "";
    private static final int pageNum = 1;
    private static final int pageSize = 5;
}
