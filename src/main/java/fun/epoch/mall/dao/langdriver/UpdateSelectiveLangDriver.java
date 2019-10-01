package fun.epoch.mall.dao.langdriver;

import java.lang.reflect.Field;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * update table_name <selective>
 */
public class UpdateSelectiveLangDriver extends AbsLangDriver {
    public static final String SELECTIVE = "<selective>";

    @Override
    public String toDynamicSQL(Class<?> parameterType, String script) {
        String sql = script.replaceAll(tag(), obj2SelectiveUpdateTags(parameterType));
        return sql2Script(sql + " where id=#{id}");
    }

    @Override
    protected String tag() {
        return SELECTIVE;
    }

    private String obj2SelectiveUpdateTags(Class<?> parameterType) {
        StringBuilder sb = new StringBuilder();

        for (Field field : parameterType.getDeclaredFields()) {
            if (include(field) || field.getName().equals("id")) {
                String fieldName = field.getName();
                String columnName = LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldName);
                String sqlTag = String.format(" <if test=\"%s != null\">%s=#{%s},</if> ", fieldName, columnName, fieldName);
                sb.append(sqlTag);
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));

        return String.format("<set>%s</set>", sb.toString());
    }
}