package fun.epoch.mall.common;

import fun.epoch.mall.utils.Settings;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统常量
 */
public class Constant {
    // 空字符串
    public static final String BLANK = "";

    // 当前登录用户
    public static final String CURRENT_USER = "currentUser";
    // 忘记密码的 Token 前缀
    public static final String FORGET_TOKEN_PREFIX = "forgetToken_";

    // 系统配置
    public static final Settings settings = new Settings();

    static {
        settings.load("mall.properties");
    }

    public interface SettingKeys {
        String PASSWORD_SALT = "password.salt";
        String IMAGE_HOST = "imageHost";
        String ALIPAY_CALLBACK_URL = "alipay.callback";
    }

    /**
     * 账号角色
     */
    public interface AccountRole {
        int MANAGER = 0;
        int CONSUMER = 1;
    }

    /**
     * 账号类型
     */
    public interface AccountType {
        String USERNAME = "username";
        String EMAIL = "email";
        String MOBILE = "mobile";
    }

    /**
     * 商品类别启用状态
     */
    public interface CategoryStatus {
        int ENABLE = 1;
        int DISABLE = 0;
    }

    /**
     * 商品销售状态
     */
    public interface SaleStatus {
        int ON_SALE = 1;
        int OFF_SALE = 0;
    }

    /**
     * 订单状态
     */
    @AllArgsConstructor
    @Getter
    public enum OrderStatus {
        CANCELED(0, "已取消"),
        UNPAID(10, "待付款"),
        PAID(30, "待发货"),
        SHIPPED(50, "待签收"),
        SUCCESS(70, "交易完成"),
        CLOSED(90, "交易关闭");

        private int code;
        private String desc;

        public static String valueOf(int code) {
            for (OrderStatus orderStatus : values()) {
                if (orderStatus.getCode() == code) {
                    return orderStatus.getDesc();
                }
            }
            return BLANK;
        }

        public static boolean contains(int code) {
            for (OrderStatus orderStatus : values()) {
                if (orderStatus.getCode() == code) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 支付类型
     */
    @AllArgsConstructor
    @Getter
    public enum PaymentType {
        ONLINE_PAY(1, "在线支付");

        private int code;
        private String desc;

        public static String valueOf(int code) {
            for (PaymentType paymentType : values()) {
                if (paymentType.getCode() == code) {
                    return paymentType.getDesc();
                }
            }
            return BLANK;
        }

        public static boolean contains(int code) {
            for (PaymentType type : values()) {
                if (type.getCode() == code) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 支付平台
     */
    @AllArgsConstructor
    @Getter
    public enum PaymentPlatform {
        ALIPAY(1, "支付宝"),
        ;

        private int code;
        private String desc;

        public static String valueOf(int code) {
            for (PaymentPlatform paymentPlatform : values()) {
                if (paymentPlatform.getCode() == code) {
                    return paymentPlatform.getDesc();
                }
            }
            return BLANK;
        }

        public static boolean contains(int code) {
            for (PaymentPlatform platform : values()) {
                if (platform.getCode() == code) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 支付宝回调状态码
     */
    public interface AlipayCallbackCode {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
}
