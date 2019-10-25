package fun.epoch.mall.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间工具
 */
public class DateTimeUtils {
    private static final Logger log = LoggerFactory.getLogger(DateTimeUtils.class);

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    public static String timeStrFrom(String date) {
        Long time = timeFrom(date);
        return time == null ? "" : time.toString();
    }

    public static Long timeFrom(String date) {
        Date theDate = from(date);
        if (theDate != null) {
            return theDate.getTime();
        }
        return null;
    }

    public static Date from(String date) {
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            log.error("从 [{}] 中获取时间日期对象失败", date, e);
        }
        return null;
    }

    public static String format(Date date) {
        return date == null ? "" : formatter.format(date);
    }
}
