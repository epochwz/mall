package fun.epoch.mall.dao.langdriver;

import java.lang.reflect.Field;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * select * from table_name <where-selective>
 */
public class WhereSelectiveLangDriver extends AbsLangDriver {
    public static final String WHERE_SELECTIVE = "<where-selective>";

    @Override
    protected String toDynamicSQL(Class<?> parameterType, String script) {
        String sql = script.replaceAll(tag(), obj2SelectiveWhereTags(parameterType));
        return sql2Script(sql);
    }

    @Override
    protected String tag() {
        return WHERE_SELECTIVE;
    }

    private String obj2SelectiveWhereTags(Class<?> parameterType) {
        StringBuilder sb = new StringBuilder();

        for (Field field : parameterType.getDeclaredFields()) {
            if (include(field) || !ignore(field)) {
                String fieldName = field.getName();
                String columnName = LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldName);
                String sqlTag;
                if (fieldName.equals("orderBy")) continue;
                if (field.getType() == String.class) {
                    String like = "CONCAT('%',#{" + fieldName + "},'%')";
                    sqlTag = String.format(" <if test=\"%s != null\"> AND %s like %s</if> ", fieldName, columnName, like);
                } else {
                    sqlTag = String.format(" <if test=\"%s != null\"> AND %s=#{%s}</if> ", fieldName, columnName, fieldName);
                }
                sb.append(sqlTag);
            }
        }

        String sql = String.format("<where>%s</where>", sb.toString());

        try {
            String fieldName = parameterType.getDeclaredField("orderBy").getName();
            sql += String.format(" <if test=\"%s != null\"> ORDER BY \\${%s} </if> ", fieldName, fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return sql;
    }

    @Override
    protected boolean include(Field field) {
        return "id".equals(field.getName());
    }
}
