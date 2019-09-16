package fun.epoch.mall.utils;

/**
 * 文本工具
 */
public class TextUtils {
    public static boolean isNotBlank(String str) {
        return str != null && str.replaceAll("\\s", "").length() > 0;
    }

    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }
}
