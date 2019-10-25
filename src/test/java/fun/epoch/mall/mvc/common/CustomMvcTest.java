package fun.epoch.mall.mvc.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import fun.epoch.mall.common.Constant;
import fun.epoch.mall.common.enhanced.MvcTestHelper;
import fun.epoch.mall.common.helper.ServerResponseHelper;
import fun.epoch.mall.entity.Category;
import fun.epoch.mall.entity.OrderItem;
import fun.epoch.mall.entity.Shipping;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.CartVo;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.ProductVo;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    public static final ServerResponseHelper<ProductVo> productHelper = new ServerResponseHelper<>(new TypeReference<ServerResponse<ProductVo>>() {
    });

    public ProductVo productVoFrom(ResultActions resultActions) {
        return productHelper.dataOf(content(resultActions));
    }

    public ProductVo productVoFrom(String resource) {
        return productHelper.dataFrom(resource);
    }

    public static final ServerResponseHelper<PageInfo<ProductVo>> productPageInfoHelper = new ServerResponseHelper<>(new TypeReference<ServerResponse<PageInfo<ProductVo>>>() {
    });

    public PageInfo<ProductVo> productPageInfoFrom(ResultActions resultActions) {
        return productPageInfoHelper.dataOf(content(resultActions));
    }

    public PageInfo<ProductVo> productPageInfoFrom(String resource) {
        return productPageInfoHelper.dataFrom(resource);
    }

    public static final ServerResponseHelper<CartVo> cartVoHelper = new ServerResponseHelper<>(new TypeReference<ServerResponse<CartVo>>() {
    });

    public CartVo cartVoFrom(ResultActions resultActions) {
        return cartVoHelper.dataOf(content(resultActions));
    }

    public CartVo cartVoFrom(String resource) {
        return cartVoHelper.dataFrom(resource);
    }

    public static final ServerResponseHelper<OrderVo> orderVoHelper = new ServerResponseHelper(new TypeReference<ServerResponse<OrderVo>>() {
    });

    public OrderVo orderVoFrom(ResultActions resultActions) {
        return orderVoHelper.dataOf(content(resultActions));
    }

    public OrderVo orderVoFrom(String resource) {
        return orderVoHelper.dataFrom(resource);
    }

    public static final ServerResponseHelper<PageInfo<OrderVo>> pageInfoHelper = new ServerResponseHelper<>(new TypeReference<ServerResponse<PageInfo<OrderVo>>>() {
    });

    public PageInfo<OrderVo> orderVoPageInfoFrom(ResultActions resultActions) {
        return pageInfoHelper.dataOf(content(resultActions));
    }

    public PageInfo<OrderVo> orderVoPageInfoFrom(String resource) {
        return pageInfoHelper.dataFrom(resource);
    }


    /* ******************** 订单 ******************** */
    public OrderVo orderVoFromAndClean(ResultActions resultActions, boolean cleanOrderNo) {
        OrderVo orderVo = orderVoFrom(resultActions);
        return cleanUnnecessaryFields(orderVo, cleanOrderNo);
    }

    public PageInfo<OrderVo> orderVoPageInfoFromAndClean(ResultActions resultActions) {
        PageInfo<OrderVo> page = orderVoPageInfoFrom(resultActions);
        page.getList().forEach(orderVo -> cleanUnnecessaryFields(orderVo, false));
        return page;
    }

    public OrderVo cleanUnnecessaryFields(OrderVo orderVo, boolean cleanOrderNo) {
        orderVo.setCreateTime(null);
        if (cleanOrderNo) orderVo.setOrderNo(null);

        Shipping shipping = orderVo.getShipping();
        if (shipping != null) {
            shipping.setUpdateTime(null);
            shipping.setCreateTime(null);
        }

        List<OrderItem> products = orderVo.getProducts();
        if (products != null) {
            products.forEach(product -> {
                if (cleanOrderNo) product.setOrderNo(null);
                product.setCreateTime(null);
                product.setUpdateTime(null);
            });
        }
        return orderVo;
    }

    public void assertSearchedSize(int expectedSize, MockHttpServletRequestBuilder searchRequest) {
        ResultActions resultActions = perform(SUCCESS, searchRequest);
        PageInfo<OrderVo> page = orderVoPageInfoFrom(resultActions);
        assertEquals(expectedSize, page.getTotal());
    }

    public void assertOrderStatus(Constant.OrderStatus expectedStatus, String api, String orderNo) {
        ResultActions resultActions = perform(SUCCESS, post(api).param("orderNo", orderNo));
        OrderVo orderVo = orderVoFrom(resultActions);
        assertEquals(expectedStatus.getCode(), orderVo.getStatus().intValue());
    }

    public void assertProductStock(int expectedStock, String api, String productId) {
        ResultActions resultActions = perform(SUCCESS, post(api).param("id", productId));
        ProductVo productVo = productVoFrom(resultActions);
        assertEquals(expectedStock, productVo.getStock().intValue());
    }
}
