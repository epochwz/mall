package fun.epoch.mall.controller.common;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.TextUtils;

import java.util.regex.Pattern;

public class Checker {
    public static final Pattern PATTERN_USERNAME = Pattern.compile("[0-9a-zA-Z_\\p{InCJKUnifiedIdeographs}-]{1,20}");
    public static final Pattern PATTERN_PASSWORD = Pattern.compile("\\S{6,32}");
    public static final Pattern PATTERN_EMAIL = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    public static final Pattern PATTERN_MOBILE = Pattern.compile("^[1][3-9]\\d{9}$");

    public static boolean checkAccountsWhenNotEmpty(User user) {
        return user != null
                && checkWhenNotEmpty(user.getUsername(), PATTERN_USERNAME)
                && checkWhenNotEmpty(user.getEmail(), PATTERN_EMAIL)
                && checkWhenNotEmpty(user.getMobile(), PATTERN_MOBILE);
    }

    public static boolean checkAccount(User user) {
        return user != null && checkUsername(user.getUsername()) && checkPassword(user.getPassword());
    }

    public static boolean checkEmail(String email) {
        return check(email, PATTERN_EMAIL);
    }

    public static boolean checkMobile(String mobile) {
        return check(mobile, PATTERN_MOBILE);
    }

    public static boolean checkUsername(String username) {
        return check(username, PATTERN_USERNAME);
    }

    public static boolean checkPassword(String password) {
        return check(password, PATTERN_PASSWORD);
    }

    public static boolean check(String str, Pattern pattern) {
        return str != null && pattern.matcher(str).matches();
    }

    public static boolean checkWhenNotEmpty(String str, Pattern pattern) {
        return TextUtils.isBlank(str) || pattern.matcher(str).matches();
    }
}
