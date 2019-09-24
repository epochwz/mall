package fun.epoch.mall.common.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件工具
 */
public class FileHelper {
    public static List<String> readLines(String resource) {
        try (
                InputStream is = Objects.requireNonNull(FileHelper.class.getClassLoader().getResourceAsStream(resource))
        ) {
            return readLines(is);
        } catch (IOException e) {
            String msg = String.format("读取资源文件 [%s] 失败: %s", resource, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    public static List<String> readLines(InputStream is) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    public static URL load(String resource) {
        return FileHelper.class.getClassLoader().getResource(resource);
    }
}
