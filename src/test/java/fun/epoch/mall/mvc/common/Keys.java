package fun.epoch.mall.mvc.common;

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
    }

    interface UserKeys {
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
    }
}
