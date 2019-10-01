package fun.epoch.mall.dao.langdriver;

/**
 * ... in <list>
 * eg. select * from table_name where id in <list>
 * eg. delete from table_name where name in <list>
 */
public class ForeachLangDriver extends AbsLangDriver {
    public static final String LIST = "<list>";

    @Override
    protected String toDynamicSQL(Class<?> parameterType, String script) {
        String sql = script.replaceAll(tag(),
                "  <foreach item=\"item\" index=\"index\" collection=\"list\"\n" +
                        "      open=\"(\" separator=\",\" close=\")\">\n" +
                        "        #{item}\n" +
                        "  </foreach>");
        return sql2Script(sql);
    }

    @Override
    protected String tag() {
        return LIST;
    }
}
