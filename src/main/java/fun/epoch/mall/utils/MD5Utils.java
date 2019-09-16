package fun.epoch.mall.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具
 */
public class MD5Utils {
    private static final Logger log = LoggerFactory.getLogger(MD5Utils.class);
    private static final String ALGORITHMS_MD5 = "MD5";
    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String encodeUTF8(String origin, String salt) {
        return salt != null ? encodeUTF8(origin + salt) : encodeUTF8(origin);
    }

    /**
     * 返回大写 MD5
     */
    private static String encodeUTF8(String origin) {
        if (origin != null) {
            try {
                byte[] originBytes = origin.getBytes(StandardCharsets.UTF_8);
                byte[] digest = MessageDigest.getInstance(ALGORITHMS_MD5).digest(originBytes);
                origin = byteArrayToHexString(digest).toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                log.error("MD5 encryption [{}] failed", origin);
            }
        }
        return origin;
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (bytes != null) {
            for (byte value : bytes) sb.append(byteToHexString(value));
        }
        return sb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }
}
