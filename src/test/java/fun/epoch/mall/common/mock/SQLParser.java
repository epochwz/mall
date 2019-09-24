package fun.epoch.mall.common.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fun.epoch.mall.utils.TextUtils.isNotBlank;

/**
 * 解析 SQL 文件
 */
public class SQLParser {
    private String tagForNewSQL;

    public SQLParser(String tagForNewSQL) {
        this.tagForNewSQL = tagForNewSQL;
    }

    public Map<String, List<String>> parse(List<String> lines) {
        Map<String, List<String>> map = new HashMap<>();

        String sqlName = null;
        StringBuilder sqlBuilder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            boolean isNewSql = line.startsWith(tagForNewSQL);
            if (!isNewSql) {
                sqlBuilder.append(line).append("\n");
            }
            boolean isLastLine = i + 1 == lines.size();
            if (isNewSql || isLastLine) {
                if (isNotBlank(sqlName)) {
                    String sql = sqlBuilder.toString().trim();
                    if (isNotBlank(sql)) {
                        List<String> sqls = map.get(sqlName);
                        if (sqls == null) {
                            sqls = new ArrayList<>();
                        }
                        sqls.add(sql);
                        map.put(sqlName, sqls);
                    }
                }
                if (!isLastLine) {
                    sqlName = line.split(tagForNewSQL)[1].trim();
                    sqlBuilder = new StringBuilder();
                }
            }
        }

        return map;
    }
}
