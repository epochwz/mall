package fun.epoch.mall.dao.langdriver;

import java.lang.reflect.Field;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * update table_name <all>
 */
public class UpdateAllLangDriver extends AbsLangDriver {
    public static final String ALL = "<all>";

    @Override
    public String toDynamicSQL(Class<?> parameterType, String script) {
        StringBuilder sb = new StringBuilder("set ");
        for (Field field : parameterType.getDeclaredFields()) {
            if (include(field)) {
                String fieldName = field.getName();
                String columnName = LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldName);
                sb.append(String.format("%s=#{%s}", columnName, fieldName)).append(", ");
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(", "));
        return script.replaceAll(tag(), sb.toString()) + " where id=#{id}";
    }

    @Override
    protected String tag() {
        return ALL;
    }
}
