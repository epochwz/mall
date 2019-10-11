package fun.epoch.mall.mvc.common;

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
    }

    interface ErrorKeys {
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
}
