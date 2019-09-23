package fun.epoch.mall.common.mock;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import fun.epoch.mall.utils.TextUtils;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模拟数据库
 */
public class DataBase {
    private DataSource dataSource;
    private List<Operation> operations;

    public DataBase(DataSource dataSource) {
        this.dataSource = dataSource;
        this.operations = new ArrayList<>();
    }

    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();

    public void launch() {
        dbSetupTracker.launchIfNecessary(new DbSetup(new DataSourceDestination(dataSource), Operations.sequenceOf(operations)));
    }

    public DataBase executeList(List<Operation> operations) {
        this.operations.addAll(operations);
        return this;
    }

    public DataBase execute(Operation... operations) {
        return executeList(Arrays.asList(operations));
    }

    public DataBase execute(List<String> sqls) {
        return sqls == null ? this : executeList(sqls.stream().filter(TextUtils::isNotBlank).map(Operations::sql).collect(Collectors.toList()));
    }

    public DataBase execute(String... sqls) {
        return execute(Arrays.asList(sqls));
    }

    public DataBase truncate(String... tables) {
        Arrays.stream(tables).filter(TextUtils::isNotBlank).map(Operations::truncate).forEach(this::execute);
        return this;
    }

    public DataBase delete(String... tables) {
        Arrays.stream(tables).filter(TextUtils::isNotBlank).map(Operations::deleteAllFrom).forEach(this::execute);
        return this;
    }

    /* ============================== 功能增强: 执行通用 SQL ============================== */
    public DataBase increment(int AutoIncrement, String... tables) {
        Arrays.stream(tables).filter(TextUtils::isNotBlank).map(table -> String.format("ALTER TABLE %s AUTO_INCREMENT = %d;", table, AutoIncrement)).forEach(this::execute);
        return this;
    }

    public DataBase reset(int AutoIncrement, String... tables) {
        return truncate(tables).increment(AutoIncrement, tables);
    }

    /* ============================== 功能扩展: 执行文件 SQL ============================== */
    private Map<String, List<String>> cache;

    public DataBase load(Map<String, List<String>> sqls) {
        if (sqls != null && sqls.size() > 0) {
            if (cache == null) this.cache = new HashMap<>();
            for (String key : sqls.keySet()) {
                List<String> list = cache.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.addAll(sqls.get(key));
                cache.put(key, list);
            }
        }
        return this;
    }

    public DataBase executeCase(String... cases) {
        Arrays.stream(cases).filter(TextUtils::isNotBlank).map(key -> cache.get(key)).forEach(this::execute);
        return this;
    }

    public DataBase prepareCase(String key, String... tablesToDelete) {
        return truncate(tablesToDelete).execute(cache.get(key));
    }

    public DataBase prepareTable(String... tables) {
        return truncate(tables).executeCase(tables);
    }

    public void launchCase(String key, String... tablesToDelete) {
        this.prepareCase(key, tablesToDelete).launch();
    }

    public void launchTable(String... tables) {
        this.prepareTable(tables).launch();
    }
}
