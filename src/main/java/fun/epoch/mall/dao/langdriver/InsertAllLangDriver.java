package fun.epoch.mall.dao.langdriver;

import java.lang.reflect.Field;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * insert into table_name <all>
 */
public class InsertAllLangDriver extends AbsLangDriver {
    public static final String ALL = "<all>";

    @Override
    public String toDynamicSQL(Class<?> parameterType, String script) {
        StringBuilder colNameBuilder = new StringBuilder();
        StringBuilder colValueBuilder = new StringBuilder();
        for (Field field : parameterType.getDeclaredFields()) {
            if (include(field)) {
                String fieldName = field.getName();
                colNameBuilder.append(LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldName)).append(", ");
                colValueBuilder.append("#{").append(fieldName).append("}").append(", ");
            }
        }
        String colNames = colNameBuilder.deleteCharAt(colNameBuilder.lastIndexOf(", ")).toString();
        String colValues = colValueBuilder.deleteCharAt(colValueBuilder.lastIndexOf(", ")).toString();
        return script.replaceAll(tag(), String.format("(%s) values (%s)", colNames, colValues));
    }

    @Override
    protected String tag() {
        return ALL;
    }
}
