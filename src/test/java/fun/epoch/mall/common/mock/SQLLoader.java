package fun.epoch.mall.common.mock;

import fun.epoch.mall.common.helper.FileHelper;

import java.util.List;
import java.util.Map;

/**
 * 加载 SQL 文件
 */
public class SQLLoader {
    private SQLParser parser;

    public SQLLoader(String tagForNewSQL) {
        this(new SQLParser(tagForNewSQL));
    }

    public SQLLoader(SQLParser parser) {
        this.parser = parser;
    }

    public Map<String, List<String>> load(String resource) {
        return parser.parse(FileHelper.readLines(resource));
    }
}
