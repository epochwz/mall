package fun.epoch.mall.mvc.common;

import java.math.BigDecimal;

import static fun.epoch.mall.common.Constant.FORGET_TOKEN_PREFIX;

/**
 * 测试常量
 */
public interface Keys {
    /**
     * 数据库表
     */
    interface Tables {
        String user = "user";
        String shipping = "shipping";
        String category = "category";
        String product = "product";
        String cart_item = "cart_item";
        String order = "`order`";
        String order_item = "order_item";
        String payment_info = "payment_info";
    }

    /**
     * SQL 测试文件
     */
    interface MockSqls {
        int AUTO_INCREMENT = 1000000;
        String COMMON_SQLS = "mock/tables.sql";
        String CART_SQLS = "mock/cart/cart.sql";
        String PRODUCT_SQLS = "mock/product/product.sql";
        String ORDER_SQLS = "mock/order/order.sql";
    }

    /**
     * 测试案例
     */
    interface MockCases {
        String CASE_CART_ALL_CHECKED = "allChecked";
        String CASE_CART_PRODUCT_QUANTITY_LIMITED = "productQuantityLimited";
        String CASE_CART_PRODUCT_NOT_EXIST = "productNotExist";
        String CASE_CART_ADD_PRODUCT_NOT_IN_CART_BEFORE = "addProductNotInCartBefore";

        String CASE_ORDER_CLOSE_ORDER = "closeOrder";
        String CASE_SEARCH_ORDER_BY_KEYWORD = "search";
        String CASE_CART_HAS_NO_CHECKED_PRODUCT = "noCheckedProduct";
        String CASE_PRODUCT_NOT_EXIST = "productNotExist";
        String CASE_PRODUCT_IS_OFF_SALE = "productOffSale";
        String CASE_PRODUCT_STOCK_NOT_ENOUGH = "productStockNotEnough";
    }

    /**
     * 预期结果
     */
    interface MockJsons {
        String EXPECTED_JSON_OF_CART_DETAIL = "mock/cart/detail.json";
        String EXPECTED_JSON_OF_CART_ALL_CHECKED = "mock/cart/detail_allChecked.json";
        String EXPECTED_JSON_OF_CART_PRODUCT_QUANTITY_LIMITED = "mock/cart/detail_productQuantityLimited.json";
        String EXPECTED_JSON_OF_CART_AFTER_UPDATE = "mock/cart/detail_afterUpdate.json";
        String EXPECTED_JSON_OF_CART_AFTER_DELETE = "mock/cart/detail_afterDelete.json";

        String EXPECTED_JSON_OF_ORDER_DETAIL = "mock/order/detail.json";
        String EXPECTED_JSON_OF_ORDER_SEARCH = "mock/order/search.json";
        String EXPECTED_JSON_OF_ORDER_PREVIEW = "mock/order/preview.json";
        String EXPECTED_JSON_OF_ORDER_CREATE = "mock/order/create.json";
    }

    interface ErrorKeys {
        Integer IdNotExist = 999999;
        String idNotExist = "999999";
        String usernameNotExist = "xiaoming";
        String passwordNotExist = "xiaoming_pass";
        String emailNotExist = "xiaoming@gmail.com";
        String mobileNotExist = "15699999999";

        String notExist = "not_exist";
    }

    interface UserKeys {
        Integer UserId = 1000000;
        String userId = "1000000";
        String username = "epoch";
        String email = "epoch@gmail.com";
        String mobile = "15622223333";
        String question = "epoch's question";
        String answer = "epoch's answer";
        String password = "epoch_pass";

        String userId2 = "1000001";
        String username2 = "epochwz";
        String email2 = "epochwz@gmail.com";
        String mobile2 = "15611112222";

        String adminId = "1000002";
        String admin = "admin";
        String adminPassword = "admin_pass";

        String forgetTokenKey = FORGET_TOKEN_PREFIX + username;
        String forgetToken = "b9f655ed-4d71-473f-abff-f989098ff818";
    }

    interface CategoryKeys {
        String parentId = "0";

        String categoryId = "1";
        String categoryName = "图书";

        String categoryId2 = "2";
        String categoryName2 = "服装";

        String newCategoryName = "食品";
    }

    interface ProductKeys {
        String productId = "1";
        String productName = "斗破苍穹";
        BigDecimal price = new BigDecimal("13.4");

        String productId2 = "2";
        String productId3 = "3";
        String productId4 = "4";

        String newProductName = "武动乾坤";

        String productIdOffSale = "5";
    }

    interface ShippingKeys {
        String shippingId = "1";
        String shippingName = "epoch";
        String shippingId2 = "2";
        String shippingName2 = "epochwz";
    }

    interface OrderKeys {
        String orderNo = "1565625618510";
        String orderCanceled = "9565625618500";
        String orderUnPaid = "9565625618511";
        String orderPaid = "9565625618533";
        String orderShipped = "9565625618555";
        String orderFinished = "9565625618577";
        String orderClosed = "9565625618599";

        String searchPrefix = "95656256185";
        String searchPrefixForTime = "8565625618";
    }
}
