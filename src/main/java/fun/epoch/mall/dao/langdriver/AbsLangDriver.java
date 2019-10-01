package fun.epoch.mall.dao.langdriver;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

@Slf4j
public abstract class AbsLangDriver extends XMLLanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        if (script.contains(tag())) {
            log.debug("origin sql: [{}]", script);
            script = toDynamicSQL(parameterType, script);
            log.debug("result sql: [{}]", script);
        }
        return super.createSqlSource(configuration, script, parameterType);
    }

    protected abstract String toDynamicSQL(Class<?> parameterType, String script);

    protected abstract String tag();

    protected boolean include(Field field) {
        return !ignore(field);
    }

    protected boolean ignore(Field field) {
        return field.isAnnotationPresent(LangDriverIgnore.class)
                || "id".equals(field.getName())
                || "createTime".equals(field.getName())
                || "updateTime".equals(field.getName());
    }

    protected String sql2Script(String sql) {
        return String.format("<script> %s </script>", sql);
    }
}
