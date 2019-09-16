package fun.epoch.mall.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static fun.epoch.mall.utils.TextUtils.isNotBlank;

/**
 * 配置工具
 */
public class Settings {
    private static final Logger log = LoggerFactory.getLogger(Settings.class);

    private Properties properties = new Properties();

    public boolean load(String resource) {
        try (
                InputStream is = Settings.class.getClassLoader().getResourceAsStream(resource)
        ) {
            properties.load(is);
            return true;
        } catch (IOException e) {
            log.error("配置文件 [{}] 加载失败：{}", resource, e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("配置文件 [{}] 加载失败：配置文件不存在！", resource, e);
        }
        return false;
    }

    public String get(String key) {
        return get(key, "");
    }

    public String get(String key, String defaultValue) {
        if (isNotBlank(key)) {
            String value = properties.getProperty(key);
            if (isNotBlank(value)) {
                return value;
            }
        }
        return defaultValue;
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Integer getInt(String key, Integer defaultValue) {
        if (isNotBlank(key)) {
            String value = properties.getProperty(key);
            if (isNotBlank(value)) {
                return Integer.valueOf(value);
            }
        }
        return defaultValue;
    }
}
