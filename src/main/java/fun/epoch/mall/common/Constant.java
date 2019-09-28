package fun.epoch.mall.common;

/**
 * 系统常量
 */
public class Constant {
    // 当前登录用户
    public static final String CURRENT_USER = "currentUser";

    /**
     * 账号角色
     */
    public interface AccountRole {
        int MANAGER = 0;
        int CONSUMER = 1;
    }
}
