package fun.epoch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static fun.epoch.utils.TextUtils.isNotBlank;

public class Settings {
    private Properties properties = new Properties();

    public Settings load(String resource) {
        try (
                InputStream is = Settings.class.getClassLoader().getResourceAsStream(resource)
        ) {
            properties.load(is);
        } catch (IOException e) {
            String errorMsg = String.format("配置文件[%s]加载失败：%s", resource, e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }
        return this;
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
