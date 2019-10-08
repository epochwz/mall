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
        String COMMON_SQLS = "mock/tables.sql";
    }

    interface UserKeys {
        String userId = "1000000";
    }
}
